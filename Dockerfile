# Используем базовый образ с установленной JDK и Gradle
FROM gradle:8.7.0-jdk17-alpine AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app
COPY . /app/.

# Собираем JAR файл проекта с помощью Gradle
RUN gradle build --no-daemon -x test

# Создаем финальный образ
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR файл собранного проекта из билдера в финальный образ
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8181
# Запускаем приложение при старте контейнера
#CMD ["java", "-jar", "app.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]


#FROM maven:3.8.4-openjdk-17 as builder
#WORKDIR /app
#COPY . /app/.
#RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true
#
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
#COPY --from=builder /app/target/*.jar /app/*.jar
#EXPOSE 8181
#ENTRYPOINT ["java", "-jar", "/app/*.jar"]