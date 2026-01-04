FROM openjdk:25-ea-1-jdk-slim

LABEL author=stanleyvladimir2005@gmail.com

EXPOSE 8080

COPY "./build/libs/spring-reactor-springboot4-0.0.1-SNAPSHOT.jar" "app.jar"

ENTRYPOINT ["java", "-jar", "/app.jar"]