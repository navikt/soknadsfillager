FROM navikt/java:11
#FROM azul/zulu-openjdk-alpine:12
ENV APPLICATION_PROFILE=docker
COPY target/soknadsfillager.jar /app/app.jar
EXPOSE 9042
