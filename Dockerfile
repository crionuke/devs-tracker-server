FROM maven:3.8.1-jdk-8 as builder
WORKDIR /build
COPY . .
RUN mvn clean package
FROM openjdk:8-jre-alpine
WORKDIR /opt
COPY --from=builder /build/target/devs-tracker-server-1.0.0.jar /opt/server.jar
COPY --from=builder /build/config/application.yml /opt/config/application.yml
CMD ["java", "-jar", "server.jar"]