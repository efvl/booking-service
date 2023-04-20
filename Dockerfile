FROM eclipse-temurin:17-jdk-alpine

COPY booking-server/target/*.jar booking-service.jar
ENTRYPOINT ["java","-jar","/booking-service.jar"]
EXPOSE 8080/tcp
