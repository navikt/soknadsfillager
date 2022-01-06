package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.api.FilesApi
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.service.DeleteFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.StoreFilesService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RestApi(
	private val getFilesService: GetFilesService,
	private val storeFilesService: StoreFilesService,
	private val deleteFilesService: DeleteFilesService
) : FilesApi {
	private val logger = LoggerFactory.getLogger(javaClass)

	/**
	 * @see FilesApi#addFiles
	 */
	override fun addFiles(fileData: List<FileData>, xInnsendingId: String?): ResponseEntity<Unit> {
		try {
			storeFilesService.storeFiles(fileData)

			logger.info("$xInnsendingId: Added files with the following ids: ${fileData.map { it.id }}")
		} catch (e: Exception) {
			logger.error("$xInnsendingId: Failed to add files with the following ids: ${fileData.map { it.id }}", e)
			throw e
		}

		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#checkFilesByIds
	 */
	override fun checkFilesByIds(ids: List<String>, xInnsendingId: String?): ResponseEntity<Unit> {
		logger.info("$xInnsendingId: Will check the status of the files with the following ids: $ids")

		getFilesService.getFiles(xInnsendingId, ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#deleteFiles
	 */
	override fun deleteFiles(ids: List<String>, xInnsendingId: String?): ResponseEntity<Unit> {
		logger.info("$xInnsendingId: Will delete the files with the following ids: $ids")

		deleteFilesService.deleteFiles(xInnsendingId, ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#findFilesByIds
	 */
	override fun findFilesByIds(ids: List<String>, xInnsendingId: String?): ResponseEntity<List<FileData>> {
		logger.info("$xInnsendingId: Will get files with the following ids: $ids")

		val files = getFilesService.getFiles(xInnsendingId, ids)
		return ResponseEntity.ok(files)
	}
}
