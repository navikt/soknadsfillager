Soknadsfillager
================
Applikasjonen tilbyr tjeneste for å lagre filer, hente filer og slette filer.
Benyttes av soknadarkiverer.
### Produksjon
![](https://github.com/navikt/soknadsfillager/workflows/Build-Deploy/badge.svg?branch=master)
![Build-Deploy-Prod](https://github.com/navikt/soknadsfillager/workflows/Build-Deploy-Prod/badge.svg?branch=master&event=deployment)

#### Akseptanse
##### q0-pipeline
![](https://github.com/navikt/soknadsfillager/workflows/Build-Deploy-Pipelines/badge.svg?branch=q0-pipeline)
![Build-Deploy-Prod](https://github.com/navikt/soknadsfillager/workflows/Build-Deploy-Pipelines/badge.svg?branch=q0-pipeline&event=deployment)
##### q1-pipeline
![](https://github.com/navikt/soknadsfillager/workflows/Build-Deploy-Pipelines/badge.svg?branch=q1-pipeline)
![Build-Deploy-Prod](https://github.com/navikt/soknadsfillager/workflows/Build-Deploy-Pipelines/badge.svg?branch=q1-pipeline&event=deployment)


## Komme i gang

### For lokal utvikling
* Bygg med `mvn clean install`
* Avhengig av at databasen kjører lokalt eller i docker
 Docker:`$ docker run -e POSTGRES_PASSWORD=postgres --name local_postgres -p 5432:5432 postgres`
Legg til `-d` for å kjøre i bakgrunnen.

### For å kjøre lokalt i docker
`$ docker run --name soknadsfillager 9042:9042`
[intelij dokumentasjon](https://www.jetbrains.com/help/idea/docker.html#)

### utviklerstøtte
For å rydde bort alle docker images som kjører lokalt:
`if [[ $(docker ps -qa) ]]; then docker stop $(docker ps -qa) ; docker rm $(docker ps -qa) ; fi; if [[ $(docker volume ls -qf dangling=true) ]]; then docker volume rm $(docker volume ls -qf dangling=true); fi`


### Henvendelser
Spørsmål knyttet til koden eller prosjektet kan rettes mot:
* [team-soknad@nav.no](mailto:team-soknad@nav.no)

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #teamsoknad
