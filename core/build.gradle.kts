plugins {
    kotlin("jvm") version "2.0.0"
    id("java-test-fixtures")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "net.azeti.recipesharing.core"
extra["testcontainersVersion"] = "1.19.8"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
}

tasks.withType<Test> {
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED", "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
    useJUnitPlatform()
}

kotlin { jvmToolchain(21) }
