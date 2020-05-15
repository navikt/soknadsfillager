package no.nav.soknad.arkivering.soknadsfillager.db

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.sql.Connection

class EmbeddedDatabase {

	companion object {
		private lateinit var embeddedPostgres: EmbeddedPostgres
		private lateinit var postgresConnection: Connection

		private lateinit var hikariConfig: HikariConfig
	}


	fun createEmbeddedSql(): HikariConfig {
		embeddedPostgres = EmbeddedPostgres.builder().start()
		postgresConnection = embeddedPostgres.postgresDatabase.connection
		hikariConfig = createHikariConfig(embeddedPostgres.getJdbcUrl("postgres", "postgres"))

		migrate(hikariConfig)
		return hikariConfig
	}

	private fun createHikariConfig(jdbcUrl: String) =
		HikariConfig().apply {
			this.jdbcUrl = jdbcUrl
			maximumPoolSize = 2
			minimumIdle = 0
			idleTimeout = 10001
			connectionTimeout = 1000
			maxLifetime = 30001
		}

	private fun migrate(config: HikariConfig) {
		Flyway.configure()
			.dataSource(HikariDataSource(config))
			.load()
			.migrate()
	}

}
