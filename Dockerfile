ARG MODULE
FROM eclipse-temurin:17-jdk-alpine AS build
ARG MODULE
WORKDIR /build

COPY pom.xml .
COPY owner-service/pom.xml owner-service/
COPY pet-service/pom.xml pet-service/
COPY web-gateway/pom.xml web-gateway/

RUN mvn dependency:go-offline -B -pl ${MODULE} -am || true

COPY owner-service owner-service/
COPY pet-service pet-service/
COPY web-gateway web-gateway/

RUN mvn package -B -pl ${MODULE} -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
ARG MODULE
WORKDIR /app
COPY --from=build /build/${MODULE}/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
