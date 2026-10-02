#!/usr/bin/env bash
# 코드 변경분을 Lambda 에 배포한다. 인프라는 infra/terraform 으로 먼저 만들어 두어야 한다.
#   빌드 → S3 업로드 → 함수 코드 교체 → 버전 발행(SnapStart 스냅샷) → live 별칭 이동
# 새 버전의 기동(스냅샷 생성)에 실패하면 별칭은 이전 버전을 그대로 가리킨다.
set -euo pipefail

APP_NAME="${APP_NAME:-knock-api}"
ALIAS="${ALIAS:-live}"
export AWS_REGION="${AWS_REGION:-ap-northeast-2}"
ZIP=build/distributions/knock-api-lambda.zip

cd "$(dirname "$0")/.."

if [[ "${SKIP_BUILD:-false}" != "true" ]]; then
  ./gradlew lambdaZip --console=plain
fi

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
BUCKET="${APP_NAME}-artifacts-${ACCOUNT_ID}"
KEY="releases/$(date +%Y%m%d-%H%M%S)-$(git rev-parse --short HEAD 2>/dev/null || echo local).zip"

echo "==> 업로드: s3://${BUCKET}/${KEY}"
aws s3 cp "$ZIP" "s3://${BUCKET}/${KEY}" --only-show-errors

echo "==> 함수 코드 교체"
aws lambda update-function-code --function-name "$APP_NAME" \
  --s3-bucket "$BUCKET" --s3-key "$KEY" --output text --query LastUpdateStatus > /dev/null
aws lambda wait function-updated-v2 --function-name "$APP_NAME"

echo "==> 버전 발행 (SnapStart 스냅샷 생성, 2~5분 소요)"
VERSION=$(aws lambda publish-version --function-name "$APP_NAME" --query Version --output text)
if ! aws lambda wait published-version-active --function-name "$APP_NAME" --qualifier "$VERSION"; then
  echo "!! v${VERSION} 기동 실패. 별칭은 이전 버전을 유지합니다. 로그: /aws/lambda/${APP_NAME}" >&2
  aws lambda get-function-configuration --function-name "$APP_NAME" --qualifier "$VERSION" \
    --query '[State, StateReason]' --output text >&2
  exit 1
fi

aws lambda update-alias --function-name "$APP_NAME" --name "$ALIAS" --function-version "$VERSION" > /dev/null
echo "==> 완료: ${APP_NAME}:${ALIAS} → v${VERSION}"
