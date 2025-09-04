# Используем JDK 21
FROM eclipse-temurin:21-jdk-alpine

# Копируем jar после сборки Gradle
COPY build/libs/app-0.0.1-SNAPSHOT.jar app.jar

# Устанавливаем рабочую директорию
WORKDIR /

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
