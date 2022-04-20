package no.nav.soknad.arkivering.soknadsfillager.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.soknad.arkivering.soknadsfillager.api.FilesTestApi
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Suppress("UastIncorrectHttpHeaderInspection")
@RestController
class TestApi : FilesTestApi {
	private val logger = LoggerFactory.getLogger(javaClass)

	/**
	 * The following annotations are copied from [FilesTestApi.addFilesTest].
	 */
	@Operation(
		summary = "Test endpoint that does nothing",
		operationId = "addFilesTest",
		description = "Endpoint used for testing that does nothing.")
	@ApiResponses(
		value = [ApiResponse(responseCode = "200", description = "Successful operation")])
	@RequestMapping(
		method = [RequestMethod.POST],
		value = ["/files-test"],
		consumes = ["application/json"]
	)
	override fun addFilesTest(
		@Parameter(description = "Files that will be added to the storage.", required = true) @Valid @RequestBody fileData: List<FileData>,
		@Parameter(description = "Tracing id that will be used in logging statements.") @RequestHeader(value = "X-innsendingId", required = false) xInnsendingId: String?
	): ResponseEntity<Unit> {

		logger.info("$xInnsendingId: TEST ENDPOINT - Adding files with the following ids: ${fileData.map { it.id }}")
		return ResponseEntity(HttpStatus.OK)
	}
}
