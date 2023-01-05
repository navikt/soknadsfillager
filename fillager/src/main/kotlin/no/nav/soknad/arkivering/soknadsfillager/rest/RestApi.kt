package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.security.token.support.core.api.Protected
import no.nav.soknad.arkivering.soknadsfillager.api.FilesApi
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.EmptyContentException
import no.nav.soknad.arkivering.soknadsfillager.service.DeleteFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.StoreFilesService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class RestApi(
	private val getFilesService: GetFilesService,
	private val storeFilesService: StoreFilesService,
	private val deleteFilesService: DeleteFilesService
) : FilesApi {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Protected
	override fun addFiles(fileData: List<FileData>, xInnsendingId: String?, xDryRun: String?): ResponseEntity<Unit> {
		if (xDryRun.isDryRunEnabled()) {
			logger.info("$xInnsendingId: Not storing the files ${fileData.map { it.id }}, because xDryRun is set to '$xDryRun'")
			return ResponseEntity(HttpStatus.OK)
		}
		try {
			storeFilesService.storeFiles(fileData)

			logger.info("$xInnsendingId: Added files with the following ids: ${fileData.map { it.id }}")
		} catch (e: EmptyContentException) {
			logger.error("$xInnsendingId: ${e.message}. The rest of the files are added")
			throw e
		} catch (e: Exception) {
			logger.error("$xInnsendingId: Failed to add files with the following ids: ${fileData.map { it.id }}", e)
			throw e
		}

		return ResponseEntity(HttpStatus.OK)
	}

	@Protected
	override fun deleteFiles(ids: List<String>, xInnsendingId: String?): ResponseEntity<Unit> {
		logger.info("$xInnsendingId: Will delete the files with the following ids: $ids")

		deleteFilesService.deleteFiles(xInnsendingId, ids)
		return ResponseEntity(HttpStatus.OK)
	}

	@Protected
	override fun findFilesByIds(ids: List<String>, metadataOnly: Boolean?, xInnsendingId: String?): ResponseEntity<List<FileData>> {
		logger.info("$xInnsendingId: Will get files with the following ids: $ids")

    if (metadataOnly == true) {
			val filesMetadata = getFilesService.getFilesMetadata(xInnsendingId, ids)
			logger.info("$xInnsendingId: The files queried have these statuses: ${filesMetadata.map { "${it.id}: ${it.status}" }}")
			return ResponseEntity.ok(filesMetadata)
		}

		return ResponseEntity.ok(getFilesService.getFiles(xInnsendingId, ids))
	}


	private fun String?.isDryRunEnabled() = this != null
}
