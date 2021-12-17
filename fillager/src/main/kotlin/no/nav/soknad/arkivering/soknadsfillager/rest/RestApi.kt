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
	override fun addFiles(fileData: List<FileData>): ResponseEntity<Unit> {
		logger.debug("Will add files with the following ids: ${fileData.map { it.id }}")

		storeFilesService.storeFiles(fileData)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#checkFilesByIds
	 */
	override fun checkFilesByIds(ids: List<String>): ResponseEntity<Unit> {
		logger.debug("Will check the status of the files with the following ids: $ids")

		getFilesService.getFiles(ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#deleteFiles
	 */
	override fun deleteFiles(ids: List<String>): ResponseEntity<Unit> {
		logger.debug("Will delete the files with the following ids: $ids")

		deleteFilesService.deleteFiles(ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * @see FilesApi#findFilesByIds
	 */
	override fun findFilesByIds(ids: List<String>): ResponseEntity<List<FileData>> {
		logger.debug("Will get files with the following ids: $ids")

		val files = getFilesService.getFiles(ids)
		return ResponseEntity.ok(files)
	}
}
