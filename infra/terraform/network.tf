data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  # RDS 서브넷 그룹은 최소 2개 AZ 가 필요하다.
  azs = slice(data.aws_availability_zones.available.names, 0, 2)
}

# Lambda 와 RDS 만 있는 프라이빗 VPC.
# Lambda 는 RDS 에만 접속하면 되므로 인터넷 게이트웨이/NAT(유료)/퍼블릭 IP(유료)가 필요 없다.
# 외부 요청은 API Gateway → Lambda 서비스 경로로 들어온다.
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true
  tags                 = { Name = var.app_name }
}

resource "aws_subnet" "private" {
  count             = length(local.azs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 8, count.index + 10)
  availability_zone = local.azs[count.index]
  tags              = { Name = "${var.app_name}-private-${local.azs[count.index]}" }
}

# ---------- 외부 DB 접속 (db_public_access = true 일 때만) ----------
# 인터넷 게이트웨이 자체는 무료. 켰을 때만 서브넷에 인터넷 경로를 연결한다.
# Lambda 는 공인 IP 가 없으므로 이 경로가 있어도 인터넷으로 나가지 않는다.
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  tags   = { Name = var.app_name }
}

resource "aws_route_table" "db_public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = { Name = "${var.app_name}-db-public" }
}

resource "aws_route_table_association" "db_public" {
  count          = var.db_public_access ? length(aws_subnet.private) : 0
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.db_public.id
}

resource "aws_security_group" "lambda" {
  name   = "${var.app_name}-lambda"
  vpc_id = aws_vpc.main.id

  egress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }
}

resource "aws_security_group" "db" {
  name   = "${var.app_name}-db"
  vpc_id = aws_vpc.main.id

  # 규칙은 모두 이 안에서만 정의한다. (별도 aws_vpc_security_group_ingress_rule 과 섞으면
  # 서로의 규칙을 지우려다 InvalidPermission.NotFound 로 실패한다)
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.lambda.id]
  }

  # db_public_access = true 일 때만 허용 IP 에서의 접속을 연다.
  dynamic "ingress" {
    for_each = var.db_public_access ? var.db_allowed_cidrs : []
    content {
      description = "External client (DataGrip)"
      from_port   = 5432
      to_port     = 5432
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }
}
