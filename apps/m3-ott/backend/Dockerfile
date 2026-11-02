FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

COPY gradlew build.gradle settings.gradle ./
COPY gradle/ ./gradle/
COPY src/ ./src/

RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder \
    /build/build/libs/ott-0.0.1-SNAPSHOT.jar \
    /app/ott.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/ott.jar"]
