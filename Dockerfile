FROM --platform=linux/amd64 maven:3-eclipse-temurin-17 AS jar-builder
COPY pom.xml pom.xml
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn clean install -Dgpg.skip=true
FROM ghcr.io/graalvm/graalvm-ce:ol9-java17-22 AS builder

WORKDIR /build

RUN gu install native-image

COPY --from=jar-builder target/**-jar-with-dependencies.jar /build

RUN native-image -jar **-jar-with-dependencies.jar app --no-fallback -H:+ReportExceptionStackTraces

FROM ubuntu:latest

COPY --from=builder /build/app /app

CMD ["/app"]