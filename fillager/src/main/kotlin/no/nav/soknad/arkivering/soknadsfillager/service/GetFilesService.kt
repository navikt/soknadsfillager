package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.ConflictException
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileGoneException
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileNotSeenException
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class GetFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun getFiles(ids: List<String>): List<FileData> {

		val files = ids.map { getFile(it) }

		if (files.map { it.status }.distinct().size != 1) {
			logger.error("Requested ids had different statuses: $files")
			throw ConflictException("Requested ids had different statuses")
		} else if (files.first().status == Status.DELETED) {
			throw FileGoneException("All the files have been deleted")
		} else if (files.first().status == Status.NOT_FOUND) {
			throw FileNotSeenException("None of the files were ever seen")
		}

		return files
			.filter { it.file != null }
			.map { it.file!! }
			.map { FileData(it.uuid, it.document!!, it.created?.atOffset(ZoneOffset.UTC) ?: OffsetDateTime.now()) }
	}

	private fun getFile(id: String): FileWithStatus {
		val timer = fileMetrics.filSummaryLatencyStart(Operations.FIND.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.FIND.name)
		try {

			val dbData = filRepository.findById(id)
			return if (!dbData.isPresent) {
				failedToFindFile(id)
			} else {
				foundFile(dbData.get())
			}

		} catch (e: Exception) {
			fileMetrics.errorCounterInc(Operations.FIND.name)
			logger.error("Failed to fetch file with id '$id'", e)
			throw e
		} finally {
			fileMetrics.filSummaryLatencyEnd(timer)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}

	private fun failedToFindFile(id: String): FileWithStatus {
		logger.info("Failed to find file with id '$id' in database")
		fileMetrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
		return FileWithStatus(id, Status.NOT_FOUND, null)
	}

	private fun foundFile(dbData: FilDbData): FileWithStatus {
		fileMetrics.filCounterInc(Operations.FIND.name)
		fileMetrics.filSummarySetSize(Operations.FIND.name, dbData.document?.size?.toDouble())
		fileMetrics.filHistogramSetSize(Operations.FIND.name, dbData.document?.size?.toDouble())
		if (dbData.document == null) {
			logger.warn("File with id '${dbData.uuid}' did not have content, i.e. was deleted.")
			return FileWithStatus(dbData.uuid, Status.DELETED, dbData)
		}
		return FileWithStatus(dbData.uuid, Status.FOUND, dbData)
	}

	private data class FileWithStatus(val id: String, val status: Status, val file: FilDbData?)
	private enum class Status { FOUND, NOT_FOUND, DELETED }
}
