FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle/

RUN ./gradlew --no-daemon dependencies

COPY . /app

RUN ./gradlew --no-daemon bootJar

EXPOSE 8080

CMD ["java", "-jar", "/app/build/libs/EthereumFetcher-0.0.1-SNAPSHOT.jar"]
