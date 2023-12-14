# Soknadsfillager

A file storage to which a client can upload, retrieve and delete files. The application stores files in a Postgres
database, and provides a Rest interface that a client can interact with.

For a description of the whole archiving system,
see [the documentation](https://github.com/navikt/archiving-infrastructure/wiki).

## Building locally

* Docker needs to be installed on the local computer in order to start a local test database for running tests.
* Build with `mvn clean install`
* Start application with `java -jar fillager/target/fillager.jar`
* There are scripts to bring up postgres and the whole archiving system locally
	in [archiving-infrastructure](https://github.com/navikt/archiving-infrastructure/).
	* Alternatively, you can run a database locally in docker:<br />
		`docker run -e POSTGRES_PASSWORD=postgres --name local_postgres -p 5432:5432 postgres -d`

## Inquiries

Questions regarding the code or the project can be asked to the team
by [raising an issue on the repo](https://github.com/navikt/soknadsfillager/issues).

### For NAV employees

NAV employees can reach the team by Slack in the channel #team-fyllut-sendinn
