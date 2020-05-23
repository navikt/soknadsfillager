package no.nav.soknad.arkivering.soknadsfillager.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import java.sql.Connection
import java.sql.ResultSet
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

class Database(private val env: AppConfiguration.DBConfig, private val vaultCredentialService: VaultCredentialService) : DatabaseInterface {
	private val logger = LoggerFactory.getLogger(javaClass)

	override val dataSource: HikariDataSource

	override val connection: Connection
		get() = dataSource.connection

	init {
		logger.info("Database init")
		runFlywayMigrations()

		val initialCredentials = vaultCredentialService.getNewCredentials(
			mountPath = env.mountPathVault,
			databaseName = env.databaseName,
			role = Role.USER
		)

		logger.info("Database init. Set datasource")
		dataSource = HikariDataSource(HikariConfig().apply {
			jdbcUrl = env.url
			username = initialCredentials.username
			password = initialCredentials.password
			maximumPoolSize = 3
			minimumIdle = 1
			idleTimeout = 10001
			maxLifetime = 300000
			isAutoCommit = false
			transactionIsolation = "TRANSACTION_REPEATABLE_READ"
			validate()
		})

		logger.info("Database init. Start RenewCredentialsTaskData")
		vaultCredentialService.renewCredentialsTaskData = RenewCredentialsTaskData(
			dataSource = dataSource,
			mountPath = env.mountPathVault,
			databaseName = env.databaseName,
			role = Role.USER
		)
	}

	private fun runFlywayMigrations() = Flyway.configure().run {
		val credentials = vaultCredentialService.getNewCredentials(
			mountPath = env.mountPathVault,
			databaseName = env.databaseName,
			role = Role.ADMIN
		)
		dataSource(env.url, credentials.username, credentials.password)
		initSql("SET ROLE \"${env.databaseName}-${Role.ADMIN}\"") // required for assigning proper owners for the tables
		logger.info("Database , runFlywayMigrations: "+"SET ROLE \"${env.databaseName}-${Role.ADMIN}\"")
		load().migrate()
	}
}

fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
	while (next()) {
		add(mapper())
	}
}

