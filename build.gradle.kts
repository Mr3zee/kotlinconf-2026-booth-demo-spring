plugins {
    java
    checkstyle
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    kotlin("plugin.jpa") version "2.3.21"
    kotlin("plugin.lombok") version "2.3.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.5"
    id("org.cyclonedx.bom") version "3.2.0"
    id("io.spring.javaformat") version "0.0.47"
    id("io.spring.nohttp") version "0.0.11"
}

gradle.startParameter.excludedTaskNames += listOf("checkFormatAot", "checkFormatAotTest")

group = "org.springframework.samples"
version = "4.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val checkstyleVersion = "12.3.1"
val springJavaformatCheckstyleVersion = "0.0.47"
val webjarsLocatorLiteVersion = "1.1.3"
val webjarsFontawesomeVersion = "4.7.0"
val webjarsBootstrapVersion = "5.3.8"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("javax.cache:cache-api")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("org.webjars:webjars-locator-lite:$webjarsLocatorLiteVersion")
    runtimeOnly("org.webjars.npm:bootstrap:$webjarsBootstrapVersion")
    runtimeOnly("org.webjars.npm:font-awesome:$webjarsFontawesomeVersion")
    runtimeOnly("com.github.ben-manes.caffeine:caffeine")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-mysql")
    checkstyle("io.spring.javaformat:spring-javaformat-checkstyle:$springJavaformatCheckstyleVersion")
    checkstyle("com.puppycrawl.tools:checkstyle:$checkstyleVersion")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named("cyclonedxDirectBom") {
    dependsOn("compileJava")
    setProperty("skipConfigs", listOf(".*[Tt]est.*"))
}

checkstyle {
    configDirectory = project.file("src/checkstyle")
    configFile = file("src/checkstyle/nohttp-checkstyle.xml")
}

tasks.named<Checkstyle>("checkstyleNohttp") {
    configDirectory = project.file("src/checkstyle")
    configFile = file("src/checkstyle/nohttp-checkstyle.xml")
}

tasks.named("formatMain") { dependsOn("checkstyleMain") }
tasks.named("formatMain") { dependsOn("checkstyleNohttp") }

tasks.named("formatTest") { dependsOn("checkstyleTest") }
tasks.named("formatTest") { dependsOn("checkstyleNohttp") }

tasks.named("checkstyleAot") { enabled = false }
tasks.named("checkstyleAotTest") { enabled = false }

tasks.named("checkFormatAot") { enabled = false }
tasks.named("checkFormatAotTest") { enabled = false }

tasks.named("formatAot") { enabled = false }
tasks.named("formatAotTest") { enabled = false }
