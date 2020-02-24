Soknadsfillager
================
Applikasjonen tilbyr tjeneste for å lagre filer, hente filer og slette filer.
Benyttes av soknadarkiverer.

## Komme i gang
* Bygg med `mvn clean install`
* Avhengig av at databasen kjører lokalt eller i docker
 Docker:`$ docker run -e POSTGRES_PASSWORD=password --name local_postgres -p 5432:5432 postgres`
Den vil da kjøre i bakgrunnen dersom man skriver
`$ docker run -d <-e....>`

### utviklerstøtte
For å rydde bort alle docker images som kjører lokalt:  
`if [[ $(docker ps -qa) ]]; then docker stop $(docker ps -qa) ; docker rm $(docker ps -qa) ; fi; if [[ $(docker volume ls -qf dangling=true) ]]; then docker volume rm $(docker volume ls -qf dangling=true); fi`


### Henvendelser
Spørsmål knyttet til koden eller prosjektet kan rettes mot:
* [mail@Team Søknad](team-soknad@nav.no)

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #teamsoknad
