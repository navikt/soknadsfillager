FROM navikt/java:11
MAINTAINER TeamSoknad
COPY target/soknadsfillager-*-SNAPSHOT-spring-boot.jar app/app.jar
EXPOSE 9042
