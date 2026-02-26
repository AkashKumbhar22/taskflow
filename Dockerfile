FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retire=3 \ 
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

  ENTRYPOINT ["java", "-jar" "app.jar"]
  