package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilMetadata
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class GetFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun getFilesMetadata(innsendingId: String?, ids: List<String>): List<FileData> {
		val timer = fileMetrics.filSummaryLatencyStart(Operations.FIND.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.FIND.name)

		try {
			val filMetadata = filRepository.findAllById(ids)
				.map { FilMetadata(it.uuid, if (it.document == null) statusDeleted else statusOk) }
			return ids
				.map { id -> filMetadata.firstOrNull { it.id == id } ?: FilMetadata(id, status = statusNotFound) }
				.map { FileData(id = it.id, status = it.status) }
				.onEach {
					if (it.status == statusOk) {
						fileMetrics.filCounterInc(Operations.FIND.name)
					} else {
						logger.info("$innsendingId: Failed to find file with id '${it.id}' in database")
						fileMetrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
					}
				}

		} catch (error: Exception) {
			fileMetrics.errorCounterInc(Operations.FIND.name)
			throw error

		} finally {
			fileMetrics.filSummaryLatencyEnd(timer)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}

	fun getFiles(innsendingId: String?, ids: List<String>): List<FileData> {
		val timer = fileMetrics.filSummaryLatencyStart(Operations.FIND.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.FIND.name)

		logger.info("$innsendingId: Skal hente ${ids.joinToString(",")}")
		try {
			val filer = filRepository.findAllById(ids)

			val idResult = ids
				.map { id ->
					filer.firstOrNull { it.uuid == id } ?: FilDbData(
						uuid = id,
						status = statusNotFound,
						created = null,
						document = null
					)
				}
				.map {
					FileData(
						id = it.uuid,
						status = if (it.status == statusNotFound) statusNotFound else if (it.document != null) statusOk else statusDeleted,
						content = it.document,
						createdAt = it.created?.atOffset(ZoneOffset.UTC)
					)
				}
				.onEach {
					if (it.status == statusOk) {
						fileMetrics.filSummarySetSize(Operations.FIND.name, it.content?.size?.toDouble())
						fileMetrics.filHistogramSetSize(Operations.FIND.name, it.content?.size?.toDouble())
						fileMetrics.filCounterInc(Operations.FIND.name)
					} else {
						logger.info("$innsendingId: Failed to find file with id '${it.id}' and status ${it.status} in database")
						fileMetrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
					}
				}

			val hentet = idResult.map { it.id + "-" + it.status }.joinToString(",")
			logger.info("$innsendingId: Hentet $hentet")
			return idResult

		} finally {
			fileMetrics.filSummaryLatencyEnd(timer)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}
}

const val statusOk = "ok"
const val statusDeleted = "deleted"
const val statusNotFound = "not-found"
