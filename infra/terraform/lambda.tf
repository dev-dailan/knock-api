resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${var.app_name}"
  retention_in_days = 14
}

data "aws_iam_policy_document" "lambda_assume" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "lambda" {
  name               = "${var.app_name}-lambda"
  assume_role_policy = data.aws_iam_policy_document.lambda_assume.json
}

# CloudWatch Logs 기록 + VPC 네트워크 인터페이스 생성 권한
resource "aws_iam_role_policy_attachment" "lambda_vpc" {
  role       = aws_iam_role.lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

# Spring Boot 앱을 코드 수정 없이 Lambda 에서 돌리기 위해 AWS Lambda Web Adapter 를 쓴다.
# 어댑터가 run.sh 로 앱을 띄운 뒤, API Gateway 이벤트를 localhost HTTP 요청으로 바꿔 전달한다.
# SnapStart 는 버전 발행 시 기동이 끝난 JVM 스냅샷을 떠 두어 콜드 스타트를 1초 안팎으로 줄인다. (Java 는 무료)
resource "aws_lambda_function" "app" {
  function_name = var.app_name
  role          = aws_iam_role.lambda.arn
  runtime       = "java21"
  architectures = ["arm64"]
  handler       = "run.sh"
  memory_size   = var.lambda_memory_mb
  timeout       = 30 # API Gateway HTTP API 의 최대 대기 시간

  s3_bucket = aws_s3_bucket.artifacts.id
  s3_key    = aws_s3_object.bootstrap.key

  layers = [
    "arn:aws:lambda:${var.aws_region}:753240598075:layer:LambdaAdapterLayerArm64:${var.lambda_web_adapter_layer_version}",
  ]

  publish = true
  snap_start {
    apply_on = "PublishedVersions"
  }

  vpc_config {
    subnet_ids         = aws_subnet.private[*].id
    security_group_ids = [aws_security_group.lambda.id]
  }

  environment {
    variables = {
      AWS_LAMBDA_EXEC_WRAPPER                  = "/opt/bootstrap"
      AWS_LWA_PORT                             = tostring(var.app_port)
      AWS_LWA_READINESS_CHECK_PATH             = "${var.app_context_path}/actuator/health/readiness"
      AWS_LWA_SNAPSTART_BEFORE_CHECKPOINT_PATH = "${var.app_context_path}/internal/snapstart/before-checkpoint"

      SPRING_PROFILES_ACTIVE = "prod"
      DB_URL                 = "jdbc:postgresql://${aws_db_instance.main.endpoint}/${var.db_name}"
      DB_USERNAME            = var.db_username
      DB_PASSWORD            = random_password.db.result
      DB_POOL_SIZE           = "2"

      # 짧게 사는 Lambda 에서는 C1 컴파일러만 쓰는 편이 기동/응답이 빠르다.
      JAVA_TOOL_OPTIONS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    }
  }

  logging_config {
    log_format = "Text"
    log_group  = aws_cloudwatch_log_group.lambda.name
  }

  lifecycle {
    # 코드 교체는 scripts/deploy.sh(또는 GitHub Actions)가 담당한다.
    ignore_changes = [s3_key, s3_object_version, source_code_hash]
  }

  depends_on = [aws_iam_role_policy_attachment.lambda_vpc]
}

# API Gateway 는 항상 이 별칭을 호출한다. 배포 = 새 버전 발행 후 별칭 이동.
resource "aws_lambda_alias" "live" {
  name             = "live"
  function_name    = aws_lambda_function.app.function_name
  function_version = aws_lambda_function.app.version

  lifecycle {
    ignore_changes = [function_version]
  }
}
