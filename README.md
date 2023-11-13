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

## Manually sending files to Soknadsfillager

If a Soknad for some reason fails to be sent through the whole archiving chain, a last resort is to manually resend it,
as explained below. You need curl and the [jo utility](https://github.com/jpmens/jo).

Run the script first against localhost:9042 (i.e. start Soknadsfillager on your own machine) to see that it works as
expected. Then do a test run against preprod (i.e. replace `soknadsfillagerurl`
with https://soknadsfillager-gcp.intern.dev.nav.no). When you have verified that the behaviour and data is as expected,
you can run against production with care.

You will be prompted with providing a password when running the script. When running locally, the password
is `password` (set in `application.yml`). For dev and prod, you can find the password in
the [GCP Secret Manager](https://console.cloud.google.com/security/secret-manager/secret/shared-innsending-secret/versions?project=team-soknad-dev-ee5e) (
remember to select the correct project - dev or prod).

The parameters you need to provide are these:

* `file`: The name of the file to upload.
* `fileid`: The id under which it will be saved in the database. If you are manually resending a failed file, this id *
	*must** match the id of the failed file in production.
* `soknadsfillagerurl`: The url to send to. This is `http://localhost:9042/files` if you run locally; if you target
	preprod or production, the ingress is set in the files in the `.nais` folder
* `innsendingId`: Id of the Soknad. This is not strictly necessary to specify, since it is only used for logging.

```
file=file_to_upload.pdf ;\
fileid=id_of_file_to_send ;\
soknadsfillagerurl=http://localhost:9042/files ;\
innsendingId=id_of_innsending ;\
jo -a $(jo id=${fileid} createdAt=$(date --utc +%FT%TZ) content=%${file}) \
| curl -X POST -H 'X-innsendingId: ${innsendingId}' -H 'Content-Type: application/json' -d @- $soknadsfillagerurl -u innsending
```

## Inquiries

Questions regarding the code or the project can be asked to the team
by [raising an issue on the repo](https://github.com/navikt/soknadsfillager/issues).

### For NAV employees

NAV employees can reach the team by Slack in the channel #team-fyllut-sendinn
