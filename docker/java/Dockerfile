#Git system environment
FROM alpine/git as clone

ARG TOKEN_GIT
ARG USERNAME_GIT
ARG REPOSITORY_SERVER

WORKDIR /app

RUN git clone https://${TOKEN_GIT}@github.com/${USERNAME_GIT}/${REPOSITORY_SERVER}.git



#Apache Maven
FROM maven:3.5-jdk-8-alpine as build

WORKDIR /app

ARG REPOSITORY_SERVER
ARG PROFILE_SERVER

COPY --from=clone /app/${REPOSITORY_SERVER} /app

RUN mvn install -Dspring-boot.run.profiles=${PROFILE_SERVER} -Djar.finalName=${REPOSITORY_SERVER}

RUN /bin/sh -c cat /app/${REPOSITORY_SERVER}/target/classes/application.yml

#Java system environment
FROM openjdk:8-jre-alpine

WORKDIR /app

ARG REPOSITORY_SERVER
ARG PROFILE_SERVER

COPY wait.sh /app/wait.sh
RUN chmod +x /app/wait.sh

ENV ARTIFACT ${REPOSITORY_SERVER}.jar

COPY --from=build /app/target/${ARTIFACT} /app

EXPOSE 8080

CMD java -jar -Dspring.profiles.active=${PROFILE_SERVER} ${ARTIFACT}