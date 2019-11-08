#FROM navikt/java:11
FROM azul/zulu-openjdk-alpine:12
MAINTAINER TeamSoknad
COPY target/soknadsfillager-spring-boot.jar app/app.jar
EXPOSE 9042
