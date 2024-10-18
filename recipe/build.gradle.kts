plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
    id("java-test-fixtures")
    id("org.flywaydb.flyway") version "10.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "net.azeti.recipesharing.recipe"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    val kotestVersion = "5.9.1"
    val mockkVersion = "1.13.11"

    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation(testFixtures(project(":core")))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:$mockkVersion")

    testFixtures(testFixtures(project(":core")))
}

tasks.withType<Test> {
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED", "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
    useJUnitPlatform()
}

kotlin { jvmToolchain(21) }
