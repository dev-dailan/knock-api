# Secrets Manager(시크릿당 월 $0.40, VPC 안에서는 엔드포인트 비용 추가) 대신
# Terraform 이 비밀번호를 만들어 Lambda 환경 변수(KMS 로 암호화 저장)로 전달한다.
# 비밀번호가 terraform.tfstate 에 남으므로 state 파일은 절대 커밋하지 않는다.
resource "random_password" "db" {
  length  = 32
  special = false
}

# 최종 스냅샷 이름이 이전 스택(knock-api-final)과 겹치면 destroy 가 실패하므로 접미사를 붙인다.
resource "random_id" "final_snapshot" {
  byte_length = 4
}

resource "aws_db_subnet_group" "main" {
  name       = var.app_name
  subnet_ids = aws_subnet.private[*].id
}

# 프리 티어: db.t4g.micro 단일 AZ 월 750시간 + 스토리지 20GB + 백업 20GB
resource "aws_db_instance" "main" {
  identifier     = var.app_name
  engine         = "postgres"
  engine_version = "17"
  instance_class = var.db_instance_class

  allocated_storage = 20
  storage_type      = "gp2"
  storage_encrypted = true

  db_name  = var.db_name
  username = var.db_username
  password = random_password.db.result

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.db.id]
  # true 면 퍼블릭 IP 가 붙고, 보안그룹에서 db_allowed_cidrs 만 허용한다. SSL 은 RDS 가 강제한다.
  publicly_accessible = var.db_public_access
  multi_az            = false

  backup_retention_period      = 1
  performance_insights_enabled = false
  apply_immediately            = true

  deletion_protection       = var.db_deletion_protection
  skip_final_snapshot       = false
  final_snapshot_identifier = "${var.app_name}-final-${random_id.final_snapshot.hex}"

  lifecycle {
    precondition {
      condition     = !var.db_public_access || length(var.db_allowed_cidrs) > 0
      error_message = "db_public_access = true 이면 db_allowed_cidrs 에 접속할 IP 를 지정해야 합니다."
    }
  }
}
