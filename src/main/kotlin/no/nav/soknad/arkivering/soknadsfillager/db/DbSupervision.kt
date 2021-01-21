package no.nav.soknad.arkivering.soknadsfillager.db

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DbSupervision(private val appConfig: AppConfiguration, private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {

	private val logger = LoggerFactory.getLogger(javaClass)
	private val delayInSeconds: Long = 5*60 // Hvert 5. minutt

	fun databaseSupervisionStart() {
		GlobalScope.launch {
			try {
				dbSupervisionTask(appConfig.applicationState)
			} catch (e: Exception) {
				logger.error("Noe gikk galt ved start av overvåking av databasebruk, prøver igjen", e.message)
				dbSupervisionTask(appConfig.applicationState)
			}
		}
	}

	private suspend fun dbSupervisionTask(applicationState: ApplicationState) {
		delay(delayInSeconds*1000) // Venter litt med å starte og sjekke databasen
		while (applicationState.ready) {
			collectDbStat()
			delay(delayInSeconds * 1000)
		}
	}

	private fun collectDbStat() {
		val count = filRepository.count()
		logger.info("Antall rader i databasen: $count")

		val documentCount = filRepository.documentCount()
		logger.info("Antall rader i databasen med dokumenter: $documentCount")

		fileMetrics.filesInDbGaugeSet(documentCount)
	}

}
