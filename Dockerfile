FROM openjdk:11-oracle
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8080 8000
CMD java -jar application.jar


