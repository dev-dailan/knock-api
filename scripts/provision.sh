#!/usr/bin/env bash
# 인프라(VPC, RDS, Lambda, API Gateway, S3, 예산 알림)를 만들거나 설정 변경을 반영한다. 최초 1회 필수.
#   빌드 → terraform init/apply → 헬스체크
# 이후 코드만 바뀌었다면 scripts/deploy.sh 를 쓴다.
set -euo pipefail

cd "$(dirname "$0")/.."
TF_DIR=infra/terraform
export AWS_REGION="${AWS_REGION:-ap-northeast-2}"

if [[ ! -f "$TF_DIR/terraform.tfvars" ]]; then
  cp "$TF_DIR/terraform.tfvars.example" "$TF_DIR/terraform.tfvars"
  echo "!! $TF_DIR/terraform.tfvars 를 만들었습니다. alert_email 을 확인한 뒤 다시 실행하세요." >&2
  exit 1
fi

aws sts get-caller-identity --query Account --output text > /dev/null

echo "==> Lambda 패키지 빌드"
./gradlew lambdaZip --console=plain

echo "==> terraform apply (최초 생성 시 RDS 5~10분 + SnapStart 스냅샷 2~5분)"
terraform -chdir="$TF_DIR" init -input=false
terraform -chdir="$TF_DIR" apply -input=false

API_URL=$(terraform -chdir="$TF_DIR" output -raw api_url)
echo "==> 헬스체크: ${API_URL}/actuator/health"
curl -fsS --retry 5 --retry-delay 5 --retry-all-errors "${API_URL}/actuator/health" && echo
echo "==> 완료. API: ${API_URL}"
