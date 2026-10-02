c#!/usr/bin/env bash
# 예전 위치(~/playground/knock-api)에서 Terraform 으로 만든 AWS 리소스를 전부 삭제한다.
#   1) RDS 삭제 보호 해제  2) terraform destroy
# RDS 는 삭제 직전에 최종 스냅샷(knock-api-final)을 남긴다. 필요 없으면 콘솔 RDS → 스냅샷에서 지운다.
# Lambda 가 VPC 에 만든 네트워크 인터페이스가 풀릴 때까지 서브넷/보안그룹 삭제가 20~40분 걸릴 수 있다.
set -euo pipefail

OLD_TF_DIR="${1:-$HOME/playground/knock-api/infra/terraform}"
export AWS_REGION="${AWS_REGION:-ap-northeast-2}"

if [[ ! -f "$OLD_TF_DIR/terraform.tfstate" ]]; then
  echo "!! $OLD_TF_DIR/terraform.tfstate 가 없습니다. 이전 스택 경로를 인자로 넘겨 주세요." >&2
  exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "==> 대상 계정: ${ACCOUNT_ID} / 리전: ${AWS_REGION}"
echo "==> 대상 state: ${OLD_TF_DIR}"
terraform -chdir="$OLD_TF_DIR" init -input=false > /dev/null
terraform -chdir="$OLD_TF_DIR" state list

read -r -p "위 리소스를 모두 삭제합니다. 계속하려면 'destroy' 입력: " answer
[[ "$answer" == "destroy" ]] || { echo "취소했습니다."; exit 1; }

echo "==> RDS 삭제 보호 해제"
terraform -chdir="$OLD_TF_DIR" apply -input=false -auto-approve \
  -target=aws_db_instance.main -var db_deletion_protection=false

echo "==> 전체 삭제"
terraform -chdir="$OLD_TF_DIR" destroy -input=false -auto-approve -var db_deletion_protection=false

echo "==> 완료. 남은 최종 스냅샷:"
aws rds describe-db-snapshots --snapshot-type manual \
  --query "DBSnapshots[?starts_with(DBSnapshotIdentifier, 'knock-api-final')].[DBSnapshotIdentifier,SnapshotCreateTime]" \
  --output text
