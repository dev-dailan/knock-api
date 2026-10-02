#!/bin/sh
# AWS Lambda Web Adapter(/opt/bootstrap)가 핸들러로 이 스크립트를 실행한다.
# 패키지에는 bootJar 가 풀린 상태로 들어 있으므로 JarLauncher 로 기동한다.
exec java -cp "$LAMBDA_TASK_ROOT" org.springframework.boot.loader.launch.JarLauncher
