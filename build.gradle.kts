plugins {
	// Kotlin
	id("org.jetbrains.kotlin.jvm") version "1.9.10"
	id("org.jetbrains.kotlin.plugin.spring") version "1.9.10"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.10"

	// Spring Boot
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.sentry.jvm.gradle") version "5.9.0"
	id("org.sonarqube") version "6.2.0.5505"

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
	maven { url = uri("https://jitpack.io") }
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-web:3.5.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
	implementation("org.springframework.boot:spring-boot-starter-actuator:3.5.0")
	implementation("org.springframework.boot:spring-boot-starter-security:3.5.0")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.5.0")
	implementation("org.springframework.boot:spring-boot-devtools:3.5.0")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.0")

	// OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

	// Utilities
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.apache.commons:commons-lang3:3.17.0")
	implementation("net.datafaker:datafaker:2.4.3")
	implementation("org.instancio:instancio-junit:5.4.1")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:4.1.1")

	// DB
	runtimeOnly("com.h2database:h2:2.3.232")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	implementation("io.sentry:sentry-spring-boot-starter:8.20.0")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
	testImplementation("org.springframework.security:spring-security-test:6.5.0")
	testImplementation(platform("org.junit:junit-bom:5.12.2"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

// Sentry
sentry {
	includeSourceContext = true
	org = "ba152fd5144f"
	projectName = "java-spring-boot-project-5"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
	includeSourceContext = false
}

sonarqube {
	properties {
		property("sonar.projectKey", "Asya-67_java-project-99")
		property("sonar.organization", "asya-67-71")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.login", System.getenv("SONAR_TOKEN"))
		property("sonar.java.coveragePlugin", "jacoco")
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
	}
}
