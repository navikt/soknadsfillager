FROM navikt/java:11

ENV APPLICATION_PROFILE=docker

COPY target/soknadsfillager.jar /app/app.jar
EXPOSE 9042
