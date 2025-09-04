# 1. Базовый образ
FROM eclipse-temurin:21-jdk-alpine

# 2. Рабочая директория
WORKDIR /app

# 3. Копируем Gradle и исходники
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# 4. Делаем gradlew исполняемым
RUN chmod +x gradlew

# 5. Сборка проекта и создание JAR
RUN ./gradlew build --no-daemon

# 6. Копируем собранный JAR в корень контейнера
RUN cp build/libs/*.jar app.jar

# 7. Порт приложения
EXPOSE 8080

# 8. Запуск через java -jar
CMD ["java", "-jar", "app.jar"]
