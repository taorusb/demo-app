FROM gradle:7.2.0-jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM adoptopenjdk:11-jre-openj9
EXPOSE 8080
RUN mkdir /app
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/demo-app-0.0.1-SNAPSHOT.jar /app/demo-app-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "demo-app-0.0.1-SNAPSHOT.jar"]