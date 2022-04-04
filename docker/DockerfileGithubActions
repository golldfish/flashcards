FROM openjdk:11-oracle
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw ./
RUN chmod +x mvnw
COPY pom.xml ./
COPY src ./src