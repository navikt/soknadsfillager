package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeleteFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun deleteFiles(innsendingId: String?, ids: List<String>) {
		ids.distinct().forEach { deleteFile(innsendingId, it) }
	}

	private fun deleteFile(innsendingId: String?, id: String) {
		val file = filRepository.findById(id)
		if (!file.isPresent) {
			logger.error("$innsendingId: Unable to find file with this id in the database: '$id'")
			return
		}

		val oppdatertFil = FilDbData(id, null, file.get().created)

		val start = fileMetrics.filSummaryLatencyStart(Operations.DELETE.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.DELETE.name)

		try {
			filRepository.saveAndFlush(oppdatertFil)

			fileMetrics.filCounterInc(Operations.DELETE.name)
		} catch (error: Exception) {
			fileMetrics.errorCounterInc(Operations.DELETE.name)
			throw error
		} finally {
			fileMetrics.filSummaryLatencyEnd(start)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}
}
