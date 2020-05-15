package no.nav.soknad.arkivering.soknadsfillager.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import java.sql.Connection
import java.sql.ResultSet
import org.flywaydb.core.Flyway

enum class Role {
	ADMIN, USER, READONLY;

	override fun toString() = name.toLowerCase()
}

class Database(private val env: AppConfiguration.DBConfig, private val vaultCredentialService: VaultCredentialService) : DatabaseInterface {
	override val dataSource: HikariDataSource

	override val connection: Connection
		get() = dataSource.connection

	init {
		runFlywayMigrations()

		val initialCredentials = vaultCredentialService.getNewCredentials(
			mountPath = env.mountPathVault,
			databaseName = env.databaseName,
			role = Role.USER
		)
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
		load().migrate()
	}
}

fun <T> ResultSet.toList(mapper: ResultSet.() -> T) = mutableListOf<T>().apply {
	while (next()) {
		add(mapper())
	}
}

interface DatabaseInterface {
	val connection: Connection
	val dataSource: HikariDataSource
}
