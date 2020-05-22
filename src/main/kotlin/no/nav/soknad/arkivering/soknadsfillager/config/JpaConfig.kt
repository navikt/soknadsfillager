package no.nav.soknad.arkivering.soknadsfillager.config

import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.db.Database
import no.nav.soknad.arkivering.soknadsfillager.db.EmbeddedDatabase
import no.nav.soknad.arkivering.soknadsfillager.db.RenewVaultService
import no.nav.soknad.arkivering.soknadsfillager.db.VaultCredentialService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JpaConfig(private val appConfig: AppConfiguration) {


	companion object {
		val vaultCredentialService = VaultCredentialService()
	}

	@Bean
	fun getDataSource(): HikariDataSource {
		return initDatasource()
	}

	private fun initDatasource(): HikariDataSource {
		when(appConfig.dbConfig.embedded) {
			true -> return HikariDataSource(EmbeddedDatabase().createEmbeddedSql())
			else -> {
				val database = Database(appConfig.dbConfig, VaultCredentialService())
				appConfig.applicationState.ready = true
				RenewVaultService(vaultCredentialService, appConfig.applicationState).startRenewTasks()
				return database.dataSource
			}
		}
	}

}


