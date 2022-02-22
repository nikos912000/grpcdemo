FROM eclipse-temurin:17.0.2_8-jdk-alpine@sha256:01383f98981b47425651fe33d8c703739984f7c6b28dbc3285fa0dd080c8f9bf
ARG JAR_FILE=service/target/service-0.0.1-SNAPSHOT-exec.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
