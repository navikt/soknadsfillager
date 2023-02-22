package no.nav.soknad.arkivering.soknadsfillager.db

import no.nav.soknad.arkivering.soknadsfillager.LeaderSelectionUtility
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class DbSupervision(
	private val filRepository: FilRepository,
	private val fileMetrics: FileMetrics,
	private val leaderSelectionUtility: LeaderSelectionUtility
) {

	private val logger = LoggerFactory.getLogger(javaClass)

	@Scheduled(cron = everyFiveMinutes)
	fun databaseSupervisionStart() {
		try {
			if (leaderSelectionUtility.isLeader()) {
				collectDbStat()
			}
		} catch (e: Exception) {
			logger.error("Something went wrong when performing database supervision", e)
		}
	}

	private fun collectDbStat() {
		val count = filRepository.count()
		logger.info("Number of rows in the database: $count")

		val documentCount = filRepository.documentCount()
		logger.info("Number of rows in the database with documents: $documentCount")

		fileMetrics.filesInDbGaugeSet(documentCount)

		val databaseSize = filRepository.totalDbSize()
		logger.info("Total database size: $databaseSize")

		fileMetrics.databaseSizeSet(databaseSize)
	}
}

private const val everyFiveMinutes = "0 */5 * * * *"
