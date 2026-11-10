FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew \
    && ./gradlew --no-daemon bootJar \
    && set -- build/libs/*.jar \
    && test "$#" -eq 1 \
    && cp "$1" /tmp/store.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /tmp/store.jar /app/store.jar
USER 10001:10001
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/store.jar"]
