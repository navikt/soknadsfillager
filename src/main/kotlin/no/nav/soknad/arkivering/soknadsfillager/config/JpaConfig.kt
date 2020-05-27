package no.nav.soknad.arkivering.soknadsfillager.config

import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.db.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JpaConfig(private val appConfig: AppConfiguration) {


	companion object {
		val vaultCredentialService = VaultCredentialService()
		val embeddedCredentialService = EmbeddedCredentialService()
	}

	@Bean
	fun getDataSource(): HikariDataSource {
		return initDatasource()
	}

	private fun initDatasource(): HikariDataSource {
		when(appConfig.dbConfig.embedded) {
			true -> {
				val database = EmbeddedDatabase(appConfig.dbConfig, embeddedCredentialService)
				appConfig.applicationState.ready = true
				EmbeddedRenewService(embeddedCredentialService, appConfig.applicationState).startRenewTasks()
				return database.dataSource
			}
			else -> {
				val database = Database(appConfig.dbConfig, vaultCredentialService)
				appConfig.applicationState.ready = true
				RenewVaultService(vaultCredentialService, appConfig.applicationState).startRenewTasks()
				return database.dataSource
			}
		}
	}

}


