package no.nav.soknad.arkivering.soknadsfillager.config

import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import no.nav.soknad.arkivering.soknadsfillager.db.Database
import no.nav.soknad.arkivering.soknadsfillager.db.EmbeddedDatabase
import no.nav.soknad.arkivering.soknadsfillager.db.RenewVaultService
import no.nav.soknad.arkivering.soknadsfillager.db.VaultCredentialService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.sql.Connection


@Configuration
class JpaConfig(private val appConfig: AppConfiguration) {

	val vaultCredentialService = VaultCredentialService()
	val applicationState = ApplicationState()

	@Bean
	open fun getDataSource(): HikariDataSource {
		val dbConfig = appConfig.dbConfig

		when(dbConfig.embedded) {
			true -> return HikariDataSource(EmbeddedDatabase().createEmbeddedSql())
			false -> {
				val database = Database(dbConfig, VaultCredentialService())
				applicationState.ready = true
				RenewVaultService(vaultCredentialService, applicationState).startRenewTasks()
				return database.dataSource
			}
		}

		throw RuntimeException("Database not initialized")

	}

}


