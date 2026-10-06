plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.3.21"
    // Spring Boot BOM 의 jOOQ 버전(jooq.version)과 맞춘다.
    id("org.jooq.jooq-codegen-gradle") version "3.21.7"
}

group = "com.alsora"
version = "0.0.1-SNAPSHOT"
description = "knock-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:postgresql")
    jooqCodegen("org.jooq:jooq-meta-extensions")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

// Liquibase changelog SQL 을 파싱해 jOOQ DSL 코드를 생성한다. (DB 접속 불필요)
jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                inputSchema = "PUBLIC"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/db/changelog/changes/*.sql"
                    }
                    property {
                        key = "sort"
                        value = "semantic"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                }
                // VARCHAR 컬럼을 Kotlin enum 으로 매핑한다. (enum 이름 그대로 저장/조회)
                forcedTypes {
                    forcedType {
                        userType = "com.alsora.knock.component.types.QuestionCategoryType"
                        isEnumConverter = true
                        includeExpression = "question_category\\.category"
                    }
                    forcedType {
                        userType = "com.alsora.knock.component.types.SupportLanguagesType"
                        isEnumConverter = true
                        includeExpression = "question_globalization\\.locale"
                    }
                    forcedType {
                        userType = "com.alsora.knock.component.types.SupportLanguagesType"
                        isEnumConverter = true
                        includeExpression = "choice_globalization\\.locale"
                    }
                    forcedType {
                        userType = "com.alsora.knock.component.types.LevelType"
                        isEnumConverter = true
                        includeExpression = "choice\\.level"
                    }
                }
            }
            target {
                packageName = "com.alsora.knock.jooq"
                directory = "build/generated-src/jooq/main"
            }
        }
    }
}

sourceSets {
    main {
        kotlin.srcDir("build/generated-src/jooq/main")
    }
}

tasks.compileKotlin {
    dependsOn(tasks.jooqCodegen)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName = "knock-api.jar"
}

tasks.jar {
    enabled = false
}

// AWS Lambda(zip + Lambda Web Adapter 레이어) 배포 패키지: build/distributions/knock-api-lambda.zip
val lambdaZip by tasks.registering(Zip::class) {
    group = "build"
    description = "Builds the AWS Lambda deployment package."
    archiveFileName = "knock-api-lambda.zip"
    destinationDirectory = layout.buildDirectory.dir("distributions")
    from(tasks.bootJar.map { zipTree(it.archiveFile) })
    from("src/lambda/run.sh") {
        filePermissions { unix("rwxr-xr-x") }
    }
}
