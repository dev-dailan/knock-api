variable "aws_region" {
  type    = string
  default = "ap-northeast-2"
}

variable "app_name" {
  type    = string
  default = "knock-api"
}

variable "vpc_cidr" {
  type    = string
  default = "10.20.0.0/16"
}

# ---------- Lambda ----------

variable "lambda_zip_path" {
  description = "최초 생성 시 업로드할 배포 패키지 (./gradlew lambdaZip 결과물)"
  type        = string
  default     = "../../build/distributions/knock-api-lambda.zip"
}

variable "lambda_memory_mb" {
  description = "메모리에 비례해 CPU 도 할당된다. 프리 티어: 월 400,000 GB-초"
  type        = number
  default     = 1024
}

variable "lambda_web_adapter_layer_version" {
  description = "AWS Lambda Web Adapter 레이어 버전 (https://github.com/awslabs/aws-lambda-web-adapter)"
  type        = number
  default     = 30
}

variable "app_port" {
  description = "application.yml 의 server.port"
  type        = number
  default     = 8082
}

variable "app_context_path" {
  description = "application.yml 의 server.servlet.context-path"
  type        = string
  default     = "/knock"
}

# ---------- API Gateway ----------

variable "api_throttle_rate" {
  description = "초당 평균 요청 한도. 과금 폭주 방지용"
  type        = number
  default     = 10
}

variable "api_throttle_burst" {
  type    = number
  default = 20
}

# ---------- RDS ----------

variable "db_instance_class" {
  description = "프리 티어 대상: db.t4g.micro / db.t3.micro"
  type        = string
  default     = "db.t4g.micro"
}

variable "db_name" {
  type    = string
  default = "knock"
}

variable "db_username" {
  type    = string
  default = "knock"
}

variable "db_deletion_protection" {
  description = "terraform destroy 전에 false 로 바꿔 apply 해야 삭제된다."
  type        = bool
  default     = true
}

variable "db_public_access" {
  description = "DataGrip 등으로 외부에서 DB 에 접속할 때만 true. 켜 둔 동안 공인 IPv4 요금(시간당 $0.005)이 붙는다."
  type        = bool
  default     = false
}

variable "db_allowed_cidrs" {
  description = "db_public_access = true 일 때 5432 접속을 허용할 IP 대역 (예: [\"203.0.113.10/32\"])"
  type        = list(string)
  default     = []

  validation {
    condition     = alltrue([for c in var.db_allowed_cidrs : can(cidrhost(c, 0)) && c != "0.0.0.0/0"])
    error_message = "올바른 CIDR 이어야 하며 0.0.0.0/0 (전체 허용)은 쓸 수 없습니다."
  }
}

# ---------- 비용 알림 ----------

variable "alert_email" {
  description = "월 예산 초과 알림을 받을 이메일. 비우면 예산을 만들지 않는다."
  type        = string
  default     = ""
}

variable "monthly_budget_usd" {
  type    = number
  default = 5
}

# ---------- GitHub Actions ----------

variable "github_repository" {
  description = "배포를 허용할 GitHub 저장소 (owner/repo). 비우면 GitHub 배포 역할을 만들지 않는다."
  type        = string
  default     = ""
}

variable "create_github_oidc_provider" {
  description = "계정에 GitHub OIDC provider 가 아직 없으면 true"
  type        = bool
  default     = true
}
