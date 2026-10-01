FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/seat-reserve-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","app.jar"]
