package no.nav.soknad.arkivering.soknadsfillager.config

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import no.nav.soknad.arkivering.soknadsfillager.db.*
import org.springframework.context.annotation.Bean
import java.io.File

private val defaultProperties = ConfigurationMap(
	mapOf(
		"APP_VERSION" to "",
		"APPLICATION_USERNAME" to "filehandler",
		"APPLICATION_PASSWORD" to "",
		"HENVENDELSE_URL" to "http://localhost:8081",
		"APPLICATION_PROFILE" to "spring",
		"REST_HENVENDELSE" to "filklient",
		"SHARED_PASSWORD" to "password",
		"DATABASE_HOST" to "localhost",
		"DATABASE_PORT" to "5432",
		"DATABASE_NAME" to "soknadsfillager",
		"DATABASE_USERNAME" to "postgres",
		"FILE_USER" to "arkiverer",
		"DATABASE_PASSWORD" to "postgres",
		"DATABASE_ADMIN_USERNAME" to "postgres",
		"DATABASE_ADMIN_PASSWORD" to "postgres",
		"DATABASE_JDBC_URL" to "",
		"DB_DRIVER" to "com.opentable.db.postgres.embedded.EmbeddedPostgres",
		"VAULT_DB_PATH" to "",
		"HENT_FRA_HENVENDELSE" to "false"
	)
)

val appConfig =
	EnvironmentVariables() overriding
		systemProperties() overriding
		ConfigurationProperties.fromResource(Configuration::class.java, "/application.yml") overriding
		defaultProperties

private fun String.configProperty(): String = appConfig[Key(this, stringType)]

fun readFileAsText(fileName: String, default: String) = try { File(fileName).readText(Charsets.UTF_8) } catch (e: Exception ) { default }

//@ConfigurationProperties
data class AppConfiguration(val restConfig: RestConfig = RestConfig(), val dbConfig: DBConfig = DBConfig()) {
	val applicationState = ApplicationState()

	data class RestConfig(
		val version: String = "APP_VERSION".configProperty(),
		val username: String = readFileAsText("/var/run/secrets/nais.io/serviceuser/username", "APPLICATION_USERNAME".configProperty()),
		val password: String = readFileAsText("/var/run/secrets/nais.io/serviceuser/password", "APPLICATION_PASSWORD".configProperty()),
		val fileUser: String = readFileAsText("/var/run/secrets/nais.io/arkiverer/username", readFileAsText("/var/run/secrets/nais.io/kv/fileUser", "FILE_USER".configProperty())),
		val sharedPassword: String = readFileAsText("/var/run/secrets/nais.io/arkiverer/password", readFileAsText("/var/run/secrets/nais.io/kv/sharedPassword", "SHARED_PASSWORD".configProperty())),
		val url: String = readFileAsText("/var/run/secrets/nais.io/kv/henvendelseUrl", "HENVENDELSE_URL".configProperty()),
		val hentFraHenvendelse: Boolean = readFileAsText("/var/run/secrets/nais.io/kv/hentFraHenvendelse", "HENT_FRA_HENVENDELSE".configProperty()).toBoolean()
	)

	data class DBConfig(
		val profiles: String = "APPLICATION_PROFILE".configProperty(),
		val databaseName: String = "DATABASE_NAME".configProperty(),
		val mountPathVault: String = "VAULT_DB_PATH".configProperty(),
		val url: String = "DATABASE_JDBC_URL".configProperty().ifBlank { null } ?: String.format(
			"jdbc:postgresql://%s:%s/%s",
			requireNotNull("DATABASE_HOST".configProperty()) { "database host must be set if jdbc url is not provided" },
			requireNotNull("DATABASE_PORT".configProperty()) { "database port must be set if jdbc url is not provided" },
			requireNotNull("DATABASE_NAME".configProperty()) { "database name must be set if jdbc url is not provided" }),
		val driver: String = readFileAsText("/var/run/secrets/nais.io/kv/dbDriver", "DB_DRIVER".configProperty()),
		val embedded: Boolean = "spring" == profiles,
		val useVault: Boolean = "dev".equals(profiles) || "prod".equals(profiles),
		val credentialService: CredentialService = if (useVault) VaultCredentialService() else EmbeddedCredentialService(),
		val renewService: RenewService = if (useVault) RenewVaultService(credentialService) else EmbeddedRenewService(credentialService)
	)

}

@org.springframework.context.annotation.Configuration
class ConfigConfig {
	@Bean
	fun appConfiguration() = AppConfiguration()
}
