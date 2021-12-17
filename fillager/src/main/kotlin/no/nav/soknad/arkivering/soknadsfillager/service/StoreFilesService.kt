package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.springframework.stereotype.Service

@Service
class StoreFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {

	fun storeFiles(files: List<FileData>) = files.forEach { storeFile(it) }

	private fun storeFile(file: FileData) {

		val start = fileMetrics.filSummaryLatencyStart(Operations.SAVE.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.SAVE.name)
		try {
			filRepository.save(FilDbData(file.id, file.content, file.createdAt.toLocalDateTime()))

			fileMetrics.filCounterInc(Operations.SAVE.name)
			fileMetrics.filSummarySetSize(Operations.SAVE.name, file.content.size.toDouble())
			fileMetrics.filHistogramSetSize(Operations.SAVE.name, file.content.size.toDouble())
		} catch (error: Exception) {
			fileMetrics.errorCounterInc(Operations.SAVE.name)
			throw error
		} finally {
			fileMetrics.filSummaryLatencyEnd(start)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}
}
