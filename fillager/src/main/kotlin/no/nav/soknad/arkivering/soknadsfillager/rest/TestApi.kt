package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.api.FilesTestApi
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@Suppress("UastIncorrectHttpHeaderInspection")
@RestController
class TestApi : FilesTestApi {
	private val logger = LoggerFactory.getLogger(javaClass)

	override fun addFilesTest(fileData: List<FileData>, xInnsendingId: String?): ResponseEntity<Unit> {

		logger.info("$xInnsendingId: TEST ENDPOINT - Adding files with the following ids: ${fileData.map { it.id }}")
		return ResponseEntity(HttpStatus.OK)
	}
}
