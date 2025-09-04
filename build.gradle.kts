plugins {
	kotlin("jvm") version "1.9.10"
	kotlin("plugin.spring") version "1.9.10"
	kotlin("plugin.jpa") version "1.9.10"

	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "6.3.1.5724"
	checkstyle
	jacoco
	application
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Demo app project for Spring Boot"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

application {
	mainClass.set("hexlet.code.AppApplication")
}

checkstyle {
	toolVersion = "10.26.1"
	configFile = rootProject.file("config/checkstyle/checkstyle.xml")
	isShowViolations = true
}

repositories {
	mavenCentral()
	maven("https://oss.sonatype.org/content/repositories/releases/")
	maven("https://jitpack.io")
}

sonar {
	properties {
		property("sonar.projectKey", "Asya-67_java-project-99")
		property("sonar.organization", "asya-67-71")
	}
}

dependencies {
	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

	// Kotlin
	implementation(kotlin("stdlib"))
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// OpenAPI nullable
	implementation("org.openapitools:jackson-databind-nullable:0.2.7")

	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Spring Security (для UserDetails и BCryptPasswordEncoder)
	implementation("org.springframework.boot:spring-boot-starter-security")

	// H2 для dev
	runtimeOnly("com.h2database:h2")

	// PostgreSQL для prod
	runtimeOnly("org.postgresql:postgresql")
	// Spring Boot Starter Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Spring Boot Starter OAuth2 Resource Server
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	// Spring Security OAuth2 JWT support
	implementation("org.springframework.security:spring-security-oauth2-jose")

	// Для работы с BCrypt
	implementation("org.springframework.security:spring-security-crypto")

	// Тесты
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.instancio:instancio-core:4.0.1")
	testImplementation ("org.instancio:instancio-junit-jupiter:4.0.1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "20"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
