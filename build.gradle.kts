plugins {
	// Kotlin
	id("org.jetbrains.kotlin.jvm") version "1.9.10"
	id("org.jetbrains.kotlin.plugin.spring") version "1.9.10"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.10"

	// Spring Boot
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"

	// Дополнительные плагины
	checkstyle
	jacoco
	application
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

application {
	mainClass.set("hexlet.code.AppApplication")
}

repositories {
	mavenCentral()
}

dependencies {
	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	// JWT
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("org.springframework.security:spring-security-crypto")

	// Databases
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	// Jakarta Servlet API
	compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
	implementation("org.openapitools:jackson-databind-nullable:0.2.7")
	implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "20"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Настройки Checkstyle
checkstyle {
	toolVersion = "10.26.1"
	configFile = rootProject.file("config/checkstyle/checkstyle.xml")
	isShowViolations = true
}

// Настройки Jacoco
jacoco {
	toolVersion = "0.8.12"
}
