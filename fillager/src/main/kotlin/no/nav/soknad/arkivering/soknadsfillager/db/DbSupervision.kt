package no.nav.soknad.arkivering.soknadsfillager.db

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class DbSupervision(
	private val appConfiguration: AppConfiguration,
	private val filRepository: FilRepository,
	private val fileMetrics: FileMetrics
) {

	private val logger = LoggerFactory.getLogger(javaClass)

	@Scheduled(cron = everyFiveMinutes)
	fun databaseSupervisionStart() {
		try {
			collectDbStat()
		} catch (e: Exception) {
			logger.error("Something went wrong when performing database supervision", e)
			appConfiguration.applicationState.alive = false
		}
	}

	private fun collectDbStat() {
		val count = filRepository.count()
		logger.info("Number of rows in the database: $count")

		val documentCount = filRepository.documentCount()
		logger.info("Number of rows in the database with documents: $documentCount")

		fileMetrics.filesInDbGaugeSet(documentCount)
	}
}

private const val everyFiveMinutes = "0 */5 * * * *"
