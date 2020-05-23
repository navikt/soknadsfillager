package no.nav.soknad.arkivering.soknadsfillager.db

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import java.sql.Connection

class EmbeddedDatabase (private val env: AppConfiguration.DBConfig, private val vaultCredentialService: EmbeddedCredentialService): DatabaseInterface {
	private val logger = LoggerFactory.getLogger(javaClass)

	companion object {
		private lateinit var embeddedPostgres: EmbeddedPostgres
		private lateinit var postgresConnection: Connection

	}

	override val dataSource: HikariDataSource

	override val connection: Connection
	get() = dataSource.connection

	init {
		logger.info("Init of embeddedPostres")
		embeddedPostgres = EmbeddedPostgres.builder().start()
		postgresConnection = embeddedPostgres.postgresDatabase.connection
		val hikariConfig = createHikariConfig(Role.ADMIN)

		runFlywayMigrations(hikariConfig)

		//hikariConfig = createHikariConfig(Role.USER)
		dataSource = HikariDataSource(hikariConfig.apply { validate() })

		logger.info("Database init. Start RenewCredentialsTaskData")
		vaultCredentialService.renewCredentialsTaskData = RenewCredentialsTaskData(
			dataSource = dataSource,
			mountPath = env.mountPathVault,
			databaseName = env.databaseName,
			role = Role.USER
		)

	}

	private fun createHikariConfig(role: Role): HikariConfig {
		return HikariConfig().apply {
			this.jdbcUrl = embeddedPostgres.getJdbcUrl("postgres", "postgres")
			maximumPoolSize = 2
			minimumIdle = 0
			idleTimeout = 10001
			connectionTimeout = 1000
			maxLifetime = 30001
		}
	}

	private fun runFlywayMigrations(config: HikariConfig) = Flyway.configure().run {
		val credentials = vaultCredentialService.getNewCredentials(
			mountPath = env.mountPathVault,
			databaseName = env.databaseName,
			role = Role.ADMIN
		)
		config.username = credentials.username
		config.password = credentials.password
		dataSource(HikariDataSource(config))
		load().migrate()
	}
}




