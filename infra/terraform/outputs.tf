output "api_url" {
  description = "API 기본 주소 (context-path 포함)"
  value       = "${aws_apigatewayv2_api.main.api_endpoint}${var.app_context_path}"
}

output "lambda_function_name" {
  value = aws_lambda_function.app.function_name
}

output "artifact_bucket" {
  value = aws_s3_bucket.artifacts.id
}

output "db_endpoint" {
  value = aws_db_instance.main.endpoint
}

output "db_name" {
  value = var.db_name
}

output "db_username" {
  value = var.db_username
}

# 확인: terraform output -raw db_password
output "db_password" {
  value     = random_password.db.result
  sensitive = true
}

output "github_deploy_role_arn" {
  value = one(aws_iam_role.github_deploy[*].arn)
}
