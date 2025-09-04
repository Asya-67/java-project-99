# Базовый образ с JDK 21
FROM eclipse-temurin:21-jdk-alpine

#Рабочая директория внутри контейнера
WORKDIR /app

# Копируем Gradle Wrapper и папку gradle
COPY gradlew .
COPY gradle gradle

# Копируем Kotlin DSL файлы сборки
COPY build.gradle.kts settings.gradle.kts ./

#Кэшируем зависимости для ускорения сборки
RUN ./gradlew dependencies --no-daemon || true

# Копируем исходники проекта
COPY src src

#Делаем gradlew исполняемым
RUN chmod +x gradlew

# Сборка Spring Boot executable JAR
RUN ./gradlew bootJar --no-daemon

#Копируем bootJar в корень контейнера и называем app.jar
RUN cp build/libs/*-SNAPSHOT.jar app.jar

# Порт приложения
EXPOSE 8080

# Команда запуска
CMD ["java", "-jar", "app.jar"]
