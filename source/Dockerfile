FROM adoptopenjdk/maven-openjdk11 as build
RUN mkdir /src
COPY azure-mongodb-error-handling-demo/src/ /src 
COPY azure-mongodb-error-handling-demo/pom.xml .
COPY application.properties /src/main/resources
RUN mvn package
FROM adoptopenjdk/openjdk11:jdk-11.0.1.13
RUN mkdir -p app \
    cd app
ADD URL_TO_THE_JAR_FILE /app/applicationinsights-agent-3.0.4-BETA-SNAPSHOT.jar
COPY --from=build /target/* /app/
COPY applicationinsights.json /app
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/applicationinsights-agent-3.0.4-BETA-SNAPSHOT.jar", "-jar", "/app/MongoDbDemo-1.0-SNAPSHOT.jar"]