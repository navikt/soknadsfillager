package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.api.FilesTestApi
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class TestApi : FilesTestApi {
	private val logger = LoggerFactory.getLogger(javaClass)

	private val seenFiles = hashSetOf<String>()
	override fun addFilesTest(fileData: List<FileData>, xInnsendingId: String?): ResponseEntity<Unit> {

		seenFiles.addAll(fileData.map { it.id })
		logger.info("$xInnsendingId: TEST ENDPOINT - Adding files with the following ids: ${fileData.map { it.id }}")
		return ResponseEntity(HttpStatus.OK)
	}

	override fun checkFilesByIdsTest(ids: List<String>, xInnsendingId: String?): ResponseEntity<Unit> {
		logger.info("$xInnsendingId: TEST ENDPOINT - Checking to see if files with these ids are present: $ids")
		val idsPresent = ids.map { seenFiles.contains(it) }
		val returnCode = if (idsPresent.all { it })
			HttpStatus.OK
		else if (idsPresent.none())
			HttpStatus.NOT_FOUND
		else
			HttpStatus.CONFLICT
		logger.info("$xInnsendingId: TEST ENDPOINT - Returning HttpStatus $returnCode")
		return ResponseEntity(returnCode)
	}
}
