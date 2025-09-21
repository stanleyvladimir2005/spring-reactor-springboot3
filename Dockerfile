FROM openjdk:25-slim

LABEL author=stanleyvladimir2005@gmail.com

EXPOSE 8080

COPY "./build/libs/spring-reactor-springboot3-0.0.1-SNAPSHOT.jar" "app.jar"

ENTRYPOINT ["java", "-jar", "/app.jar"]