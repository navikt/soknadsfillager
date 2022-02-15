FROM navikt/java:17

ENV APPLICATION_PROFILE=docker

COPY fillager/target/*.jar /app/app.jar
EXPOSE 9042
