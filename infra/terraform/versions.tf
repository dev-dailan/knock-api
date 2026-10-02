terraform {
  required_version = ">= 1.6"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }

  # 팀 협업 시 원격 state 사용을 권장합니다. S3 버킷을 먼저 만든 뒤 주석을 해제하세요.
  # backend "s3" {
  #   bucket       = "knock-terraform-state"
  #   key          = "knock-api/terraform.tfstate"
  #   region       = "ap-northeast-2"
  #   use_lockfile = true
  # }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project   = var.app_name
      ManagedBy = "terraform"
    }
  }
}
