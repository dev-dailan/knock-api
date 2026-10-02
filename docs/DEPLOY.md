# AWS 서버리스 배포 가이드 (프리 티어)

## 0. 빠른 시작: 이전 배포 삭제 → 새로 배포

예전 위치(`~/playground/knock-api`)에서 만든 스택을 지우고 이 프로젝트로 다시 만듭니다.
AWS CLI 로그인(2-3 장)이 되어 있어야 합니다. 프로젝트 루트에서 순서대로 실행하세요.

```bash
./scripts/destroy-old.sh
```

- 이전 스택의 RDS 삭제 보호를 풀고 `terraform destroy` 합니다. (`destroy` 입력으로 확인)
- RDS 데이터는 최종 스냅샷 `knock-api-final` 로 남습니다. 필요 없으면 콘솔 **RDS → 스냅샷**에서 삭제하세요.
- Lambda 의 VPC 네트워크 인터페이스 해제 때문에 20~40분 걸릴 수 있습니다.

```bash
./scripts/provision.sh
```

- `./gradlew lambdaZip` → `terraform apply` (`yes` 입력) → 헬스체크까지 진행합니다.
- 설정값은 `infra/terraform/terraform.tfvars` (git 제외). 이전 프로젝트의 값을 그대로 복사해 두었습니다.
- 이후 코드만 바뀌면 `./scripts/deploy.sh` (7장) 만 실행하면 됩니다.

| 스크립트 | 용도 | 언제 |
|---|---|---|
| `scripts/destroy-old.sh` | 이전 스택 삭제 | 이번 1회 |
| `scripts/provision.sh` | 인프라 생성/설정 변경 반영 | 최초 1회, Terraform 수정 시 |
| `scripts/deploy.sh` | 코드 배포 (새 Lambda 버전 → `live` 이동) | 코드 수정 시마다 |


## 1. 구성

```
클라이언트
   │ HTTPS
   ▼
API Gateway (HTTP API, 초당 10건 제한)
   │
   ▼
Lambda  knock-api:live  (java21 / arm64 / SnapStart)
 ├─ Lambda Web Adapter 레이어: API Gateway 이벤트 ↔ localhost:8082 HTTP 변환
 └─ Spring Boot 앱 (코드 수정 없이 그대로 실행)
   │ 5432 (VPC 내부)
   ▼
RDS PostgreSQL 17  db.t4g.micro  (프라이빗 서브넷, 인터넷 노출 없음)
```

| 리소스 | 프리 티어 | 프리 티어 이후 (서울, 대략) |
|---|---|---|
| Lambda | **상시 무료**: 월 100만 요청 + 40만 GB-초 | 소량 트래픽이면 거의 $0 |
| SnapStart (Java) | 추가 요금 없음 | 〃 |
| API Gateway HTTP API | 12개월간 월 100만 건 | 100만 건당 약 $1 |
| RDS db.t4g.micro + 20GB | 12개월간 월 750시간 | **월 약 $20** ← 비용 대부분 |
| S3 / CloudWatch Logs | 사용량 미미 | 수 센트 |
| NAT Gateway, ALB | **사용 안 함** | 설계상 과금 없음 |
| 퍼블릭 IPv4 (DB 외부 접속을 켰을 때만) | 12개월간 월 750시간 | 켜 둔 시간당 $0.005 (한 달 내내 약 $3.6) |

> ⚠️ **프리 티어 조건 확인**: 2025-07-15 이후 가입한 계정은 기존 "12개월 무료" 대신
> **크레딧 기반 Free plan**(가입 크레딧을 6개월 동안 사용)이 적용됩니다. 이 경우 RDS 도
> 크레딧에서 차감됩니다. 콘솔 **Billing and Cost Management → Free Tier / Credits** 에서
> 본인 계정 조건을 먼저 확인하세요. Lambda 의 월 100만 요청 무료는 계정 종류와 상관없이 적용됩니다.

비용을 지키기 위한 설정 (Terraform 에 반영되어 있음):
- NAT Gateway / 인터넷 게이트웨이 없는 프라이빗 VPC → 시간당 요금 리소스 없음
- DB 비밀번호는 Secrets Manager(유료) 대신 Terraform 이 만들어 Lambda 환경 변수로 전달
- API Gateway 스로틀링(초당 10, 버스트 20)으로 호출 폭주 차단
- RDS: 단일 AZ, gp2 20GB 고정(자동 확장 없음), 백업 1일, Performance Insights 끔
- AWS Budgets 월 $5 초과 예상 시 메일 알림 (`alert_email` 입력 시)

---

## 2. 사전 준비 (최초 1회)

### 2-1. AWS 계정과 IAM 사용자

1. <https://aws.amazon.com> 에서 계정 생성 후 **루트 계정에 MFA** 설정
2. 루트 계정 대신 작업용 사용자를 만듭니다.
   - 콘솔 → **IAM Identity Center** (권장) 또는 **IAM → 사용자 → 사용자 생성**
   - 권한: 처음에는 `AdministratorAccess` (Terraform 이 VPC·IAM·RDS 등을 만들기 때문)
   - IAM 사용자라면 **보안 자격 증명 → 액세스 키 만들기 → CLI** 로 키 발급
3. 오른쪽 위 리전을 **아시아 태평양(서울) ap-northeast-2** 로 선택

### 2-2. 로컬 도구 설치 (macOS)

```bash
brew install awscli
```

```bash
brew tap hashicorp/tap && brew install hashicorp/tap/terraform
```

확인:

```bash
aws --version && terraform -version && java -version
```

Java 21, Docker(테스트용) 는 이미 설치되어 있어야 합니다.

### 2-3. AWS CLI 로그인

IAM 사용자 액세스 키를 쓰는 경우:

```bash
aws configure
```

`AWS Access Key ID`, `Secret Access Key`, `Default region name: ap-northeast-2`, `output: json` 입력.
IAM Identity Center 를 쓰는 경우 `aws configure sso` 로 설정한 뒤 `export AWS_PROFILE=<프로필명>`.

연결 확인 (계정 ID 가 출력되면 성공):

```bash
aws sts get-caller-identity
```

---

## 3. 배포 전 앱 확인 사항

### 3-1. 설정값 — `application.yml` 과 Terraform 이 맞아야 합니다

| application.yml | Terraform 변수 | 현재 값 |
|---|---|---|
| `server.port` | `app_port` | `8082` |
| `server.servlet.context-path` | `app_context_path` | `/knock` |

둘 중 하나를 바꾸면 다른 쪽도 같이 바꿔야 Lambda Web Adapter 가 앱을 찾습니다.

DB 접속 정보(`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)는 **환경 변수로만** 받습니다.
`application.yml` 에 비밀번호를 직접 적지 마세요 (Git 에 올라가면 유출).

| 실행 위치 | 값을 넣는 곳 | 할 일 |
|---|---|---|
| AWS Lambda (`prod`) | Terraform 이 RDS 를 만들면서 비밀번호를 생성하고 **Lambda 환경 변수로 자동 주입** (`infra/terraform/lambda.tf`) | 없음 |
| 로컬 IntelliJ / `./gradlew bootRun` (`local`) | 프로젝트 루트의 `.env` (`cp .env.example .env`, git 제외) | `.env` 수정 |
| 로컬 `docker compose --profile app up` (`prod`) | `docker-compose.yml` 의 `environment` (값은 `.env` 에서 가져옴) | `.env` 수정 |

`prod` 프로필에는 기본값이 없어서, 값이 빠지면 엉뚱한 DB 에 조용히 붙지 않고 기동에 실패합니다.
AWS 에 넣은 값은 콘솔 **Lambda → knock-api → 구성 → 환경 변수** 에서 확인할 수 있습니다.

### 3-2. 초기 데이터 넣기

두 가지 방법이 있습니다.
- **DataGrip 으로 직접 입력**: 배포 후 [6장](#6-datagrip-으로-db-접속) 방법으로 접속해 INSERT
- **Liquibase changeset**: 코드와 함께 버전 관리되고, 앱이 기동할 때 자동 적용됩니다. 아래 예시 참고

`src/main/resources/db/changelog/changes/0002-seed-question.sql`

```sql
--liquibase formatted sql

--changeset knock:0002-seed-question logicalFilePath:db/changelog/changes/0002-seed-question.sql
INSERT INTO question (content) VALUES
    ('이름이 뭐예요?'),
    ('전화번호가 뭐예요?'),
    ('주소가 뭐예요?');
--rollback DELETE FROM question WHERE content IN ('이름이 뭐예요?', '전화번호가 뭐예요?', '주소가 뭐예요?');
```

질문을 더 추가할 때는 이 파일을 고치지 말고 `0003-...sql` 처럼 **새 파일**을 만듭니다.

### 3-3. 로컬 테스트 & 패키지 빌드

```bash
./gradlew test
```

```bash
./gradlew lambdaZip
```

`build/distributions/knock-api-lambda.zip` (약 60MB) 이 생성됩니다.
zip 안에는 풀린 bootJar 와 `run.sh`(앱 기동 스크립트)가 들어 있습니다.

---

## 4. 인프라 생성 (최초 1회)

```bash
cd infra/terraform
```

```bash
cp terraform.tfvars.example terraform.tfvars
```

`terraform.tfvars` 를 열어 `alert_email` 에 본인 메일을 넣습니다.
GitHub 자동 배포를 쓸 거라면 `github_repository = "owner/repo"` 도 입력합니다 (나중에 해도 됨).

```bash
terraform init
```

만들어질 리소스 확인 (약 25개, `+ create` 만 있어야 정상):

```bash
terraform plan
```

생성 (RDS 생성에 5~10분, SnapStart 첫 스냅샷에 2~5분 걸립니다):

```bash
terraform apply
```

`yes` 입력. 완료되면 출력값이 나옵니다.

```
api_url              = "https://abcd1234.execute-api.ap-northeast-2.amazonaws.com/knock"
artifact_bucket      = "knock-api-artifacts-123456789012"
lambda_function_name = "knock-api"
...
```

> `terraform.tfstate` 에 DB 비밀번호가 들어 있습니다. `.gitignore` 에 이미 제외되어 있으니
> 커밋하지 말고, 이 파일을 잃어버리지 않도록 백업해 두세요.
> (여러 명이 쓸 경우 `versions.tf` 의 S3 backend 주석 참고)

---

## 5. 동작 확인

```bash
API_URL=$(terraform output -raw api_url)
```

```bash
curl -s "$API_URL/questions/random"
```

```bash
curl -s "$API_URL/actuator/health"
```

실시간 로그 보기:

```bash
aws logs tail /aws/lambda/knock-api --follow
```

- 오래 호출이 없다가 들어온 첫 요청은 SnapStart 복원 때문에 약 1초 정도 더 걸립니다.
- `/internal/snapstart/...` 경로는 외부에서 호출하면 403 이 정상입니다 (Web Adapter 가 차단).

---

## 6. DataGrip 으로 DB 접속

평소 RDS 는 인터넷에서 접근할 수 없습니다. 접속이 필요할 때만 **내 IP 에 한해** 열고, 끝나면 닫습니다.
열어 둔 시간만큼 퍼블릭 IPv4 요금(시간당 $0.005)이 붙습니다.

### 6-1. 접속 열기

내 공인 IP 확인:

```bash
curl -s https://checkip.amazonaws.com
```

`infra/terraform/terraform.tfvars` 에 추가 (IP 뒤에 `/32`):

```hcl
db_public_access = true
db_allowed_cidrs = ["203.0.113.10/32"]
```

`infra/terraform` 에서 적용 (RDS 설정 변경에 수 분 소요, 재시작 없음):

```bash
terraform apply
```

### 6-2. 접속 정보 확인

```bash
terraform output db_endpoint
```

```bash
terraform output -raw db_password
```

### 6-3. DataGrip 설정

1. **+ → Data Source → PostgreSQL**
2. General 탭
   - Host: `db_endpoint` 에서 `:5432` 앞부분 (예: `knock-api.xxxx.ap-northeast-2.rds.amazonaws.com`)
   - Port: `5432`
   - User: `knock` / Password: 6-2 의 값
   - Database: `knock`
3. SSH/SSL 탭 → **Use SSL** 체크, Mode: `require` (RDS 는 SSL 이 아닌 접속을 거부합니다)
4. **Test Connection** → 성공하면 OK

여기서 `question` 테이블에 데이터를 직접 넣고 고칠 수 있습니다.
단, **테이블 구조 변경(ALTER 등)은 DataGrip 에서 하지 말고** Liquibase changeset 으로 하세요.
Hibernate 가 `ddl-auto: validate` 로 검증하므로 어긋나면 다음 배포가 실패합니다.

### 6-4. 접속 닫기 (작업 후 꼭)

`terraform.tfvars` 에서 `db_public_access = false` 로 바꾸고:

```bash
terraform apply
```

- IP 가 바뀌어(카페, 재부팅 등) 접속이 안 되면 `db_allowed_cidrs` 를 새 IP 로 고쳐 다시 apply 합니다.
- 접속을 열고 닫는 동안에도 API(Lambda)는 영향 없이 계속 동작합니다.

---

## 7. 코드 수정 후 재배포

프로젝트 루트에서:

```bash
./scripts/deploy.sh
```

스크립트가 하는 일:
1. `./gradlew lambdaZip`
2. S3 `releases/` 에 zip 업로드 (7일 후 자동 삭제)
3. Lambda 코드 교체 → **새 버전 발행** (SnapStart 가 앱을 기동해 스냅샷 생성)
4. 기동에 성공하면 `live` 별칭을 새 버전으로 이동 (API Gateway 는 항상 `live` 를 호출)

기동에 실패하면(예: DB 마이그레이션 오류) 별칭이 움직이지 않으므로 **기존 버전이 계속 서비스**됩니다.

이전 버전으로 되돌리기:

```bash
aws lambda list-versions-by-function --function-name knock-api --query 'Versions[].Version'
```

```bash
aws lambda update-alias --function-name knock-api --name live --function-version <이전 버전 번호>
```

> Terraform 으로 Lambda 설정(메모리, 환경 변수 등)을 바꾼 뒤에는 `./scripts/deploy.sh` 를
> 한 번 더 실행해야 새 설정이 담긴 버전으로 `live` 가 이동합니다.

---

## 8. GitHub Actions 자동 배포 (선택)

`main` 에 push 하면 테스트 → `scripts/deploy.sh` 가 자동 실행됩니다. AWS 키를 GitHub 에 저장하지 않고 OIDC 로 인증합니다.

1. GitHub 에 저장소를 만들고 코드를 push
2. `infra/terraform/terraform.tfvars` 에 `github_repository = "owner/repo"` 입력 후 `terraform apply`
   - 계정에 GitHub OIDC provider 가 이미 있으면 `create_github_oidc_provider = false` 도 추가
3. 역할 ARN 확인: `terraform output -raw github_deploy_role_arn`
4. GitHub 저장소 **Settings → Environments → New environment** → `production`
5. **Settings → Secrets and variables → Actions → Variables 탭 → New repository variable**
   - Name: `AWS_DEPLOY_ROLE_ARN`, Value: 3번의 ARN
6. Actions 탭에서 **Deploy → Run workflow** 로 수동 실행해 확인

변수가 없으면 배포 잡은 건너뛰고 테스트만 실행됩니다.

---

## 9. 문제 해결

| 증상 | 원인 / 확인 방법 |
|---|---|
| `terraform apply` 중 Lambda 버전 생성 실패, `deploy.sh` 가 "기동 실패" | 앱이 기동하지 못함. `aws logs tail /aws/lambda/knock-api --since 30m` 로 스택트레이스 확인 (DB 접속, Liquibase, 엔티티-스키마 불일치(`ddl-auto: validate`) 가 흔함) |
| `curl` 결과 `{"message":"Internal Server Error"}` | Lambda 실행 중 오류 또는 30초 초과. 위 로그 확인 |
| `{"message":"Too Many Requests"}` | API Gateway 스로틀링. `api_throttle_rate` 조정 |
| `{"message":"Not Found"}` 가 아닌 Spring 404 | 경로에 context-path `/knock` 가 빠졌는지 확인 |
| Lambda 레이어를 찾을 수 없다는 오류 | `lambda_web_adapter_layer_version` 을 [최신 버전](https://github.com/awslabs/aws-lambda-web-adapter#lambda-functions-packaged-as-zip-package-for-aws-managed-runtimes)으로 변경 |
| DataGrip 접속 타임아웃 | `db_public_access = true` 적용 여부, 현재 IP 가 `db_allowed_cidrs` 와 같은지, 회사망에서 5432 포트가 막혀 있지 않은지 확인 |
| DataGrip `no pg_hba.conf entry ... no encryption` | SSL 을 켜지 않음 → 6-3 의 3번 |
| RDS 생성 시 백업/인스턴스 관련 Free plan 제한 오류 | 계정이 Free plan 이면 일부 옵션이 제한됩니다. 오류 메시지의 허용 값으로 `database.tf` 조정 |

비용 확인: 콘솔 **Billing and Cost Management → Bills / Free Tier** 에서 서비스별 사용량을 주기적으로 보세요.

---

## 10. 전체 삭제

더 이상 쓰지 않으면 RDS 요금이 계속 나가므로 삭제합니다.

1. `terraform.tfvars` 에 `db_deletion_protection = false` 추가 후 `terraform apply`
2. 삭제:

```bash
terraform destroy
```

3. 삭제 시 RDS 최종 스냅샷(`knock-api-final`)이 남습니다. 필요 없으면 콘솔 **RDS → 스냅샷**에서 삭제하세요 (보관 용량만큼 과금).
