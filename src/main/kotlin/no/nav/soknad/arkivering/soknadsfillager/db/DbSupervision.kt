package no.nav.soknad.arkivering.soknadsfillager.db

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DbSupervision(private val filRepository: FilRepository,
										private val fileMetrics: FileMetrics) {

	private val logger = LoggerFactory.getLogger(javaClass)

	@Scheduled(cron = everyFiveMinutes, initialDelay = 10_000)
	fun databaseSupervisionStart() {
		try {
			collectDbStat()
		} catch (e: Exception) {
			logger.error("Something went wrong when performing database supervision", e.message)
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

private const val everyFiveMinutes = "*/5 * * * *"
