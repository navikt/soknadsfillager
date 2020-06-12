package no.nav.soknad.arkivering.soknadsfillager.config

import com.zaxxer.hikari.HikariDataSource
import no.nav.soknad.arkivering.soknadsfillager.db.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JpaConfig(private val appConfig: AppConfiguration) {

	@Bean
	fun getDataSource(): HikariDataSource {
		return initDatasource()
	}

	private fun initDatasource(): HikariDataSource {
		val database = if (appConfig.dbConfig.embedded) {
			EmbeddedDatabase(appConfig.dbConfig, appConfig.dbConfig.credentialService)
		} else {
			Database(appConfig.dbConfig, appConfig.dbConfig.credentialService)
		}
		appConfig.applicationState.ready = true
		appConfig.dbConfig.renewService.startRenewTasks(appConfig.applicationState)
		return database.dataSource
	}

}


