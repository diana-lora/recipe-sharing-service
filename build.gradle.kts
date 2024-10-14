plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
    id("org.flywaydb.flyway") version "10.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    jacoco
    java
}

group = "net.azeti.challenge.recipe"
extra["testcontainersVersion"] = "1.19.8"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    val postgresVersion = "42.7.3"
    val flywayVersion = "10.17.0"
    val passayVersion = "1.6.4"
    val kotestVersion = "5.9.1"
    val mockkVersion = "1.13.11"
    val openApiVersion = "2.6.0"
    val okhttp3Version = "4.12.0"
    val jsonwebtokenVersion = "0.11.5" // fixme update version
    val wiremockVersion = "3.0.1"

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtokenVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jsonwebtokenVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jsonwebtokenVersion")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")
    implementation("org.passay:passay:$passayVersion")

    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
}

tasks.withType<Test> {
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED", "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

kotlin { jvmToolchain(21) }

jacoco { toolVersion = "0.8.11" }

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports { xml.required.set(true) }
}
