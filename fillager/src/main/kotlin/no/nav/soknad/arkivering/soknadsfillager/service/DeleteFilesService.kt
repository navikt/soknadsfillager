package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeleteFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun deleteFiles(innsendingId: String?, ids: List<String>) {
		val start = fileMetrics.filSummaryLatencyStart(Operations.DELETE.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.DELETE.name)

		try {
			val countDelRecords = filRepository.deleteFiles(ids)
			if (ids.size != countDelRecords)
				logger.warn(
					"$innsendingId: Number of deleted records ($countDelRecords) does not match the number of " +
						"requested deletions (${ids.size}). Ids: $ids"
				)

			(0 until countDelRecords).forEach { _ -> fileMetrics.filCounterInc(Operations.DELETE.name) }
			(0 until ids.size - countDelRecords).forEach { _ -> fileMetrics.errorCounterInc(Operations.DELETE.name) }
		} catch (e: Exception) {
			fileMetrics.errorCounterInc(Operations.DELETE.name)
			throw e
		} finally {
			fileMetrics.filSummaryLatencyEnd(start)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}
}
