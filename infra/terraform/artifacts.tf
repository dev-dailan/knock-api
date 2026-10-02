data "aws_caller_identity" "current" {}

# Lambda 배포 패키지(zip)가 50MB 를 넘으므로 S3 를 거쳐 업로드한다.
resource "aws_s3_bucket" "artifacts" {
  bucket        = "${var.app_name}-artifacts-${data.aws_caller_identity.current.account_id}"
  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "artifacts" {
  bucket                  = aws_s3_bucket.artifacts.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Lambda 는 코드 사본을 따로 보관하므로 업로드한 zip 은 오래 둘 필요가 없다.
resource "aws_s3_bucket_lifecycle_configuration" "artifacts" {
  bucket = aws_s3_bucket.artifacts.id

  rule {
    id     = "expire-releases"
    status = "Enabled"
    filter {
      prefix = "releases/"
    }
    expiration {
      days = 7
    }
  }
}

# 최초 1회 생성용. 이후 코드는 scripts/deploy.sh 가 releases/ 에 올려 교체한다.
resource "aws_s3_object" "bootstrap" {
  bucket = aws_s3_bucket.artifacts.id
  key    = "bootstrap/knock-api-lambda.zip"
  source = var.lambda_zip_path

  lifecycle {
    ignore_changes = [source, etag, source_hash]
  }
}
