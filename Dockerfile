FROM navikt/java:11
#FROM azul/zulu-openjdk-alpine:12

COPY target/soknadsfillager.jar /app/app.jar
EXPOSE 9042