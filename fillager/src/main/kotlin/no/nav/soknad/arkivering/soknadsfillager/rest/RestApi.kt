package no.nav.soknad.arkivering.soknadsfillager.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.soknad.arkivering.soknadsfillager.api.FilesApi
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.service.DeleteFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.StoreFilesService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Suppress("UastIncorrectHttpHeaderInspection")
@RestController
class RestApi(
	private val getFilesService: GetFilesService,
	private val storeFilesService: StoreFilesService,
	private val deleteFilesService: DeleteFilesService
) : FilesApi {
	private val logger = LoggerFactory.getLogger(javaClass)

	/**
	 * The following annotations are copied from [FilesApi.addFiles].
	 */
	@Operation(
		summary = "Add new files to the file storage.",
		operationId = "addFiles",
		description = "Adds new files to the file storage. If a file already exists with the given id, the old file is overwritten.",)
	@ApiResponses(
		value = [ApiResponse(responseCode = "200", description = "Successful operation")])
	@RequestMapping(
		method = [RequestMethod.POST],
		value = ["/files"],
		consumes = ["application/json"]
	)
	override fun addFiles(
		@Parameter(description = "Files that will be added to the storage.", required = true) @Valid @RequestBody fileData: List<FileData>,
		@Parameter(description = "Tracing id that will be used in logging statements.") @RequestHeader(value = "X-innsendingId", required = false) xInnsendingId: String?
	): ResponseEntity<Unit> {
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
	 * The following annotations are copied from [FilesApi.checkFilesByIds].
	 */
	@Operation(
		summary = "Checks if the files with given ids exist.",
		operationId = "checkFilesByIds",
		description = "Given a list of ids, this endpoint responds with whether all the files associated with those ids exists in the file storage. This endpoint checks whether the files exist but will not return them.")
	@ApiResponses(
		value = [
			ApiResponse(responseCode = "200", description = "Successful operation, **ALL** of the requested files were found."),
			ApiResponse(responseCode = "404", description = "File Not Found, **NONE** of the requested files have ever been seen in the file storage."),
			ApiResponse(responseCode = "409", description = "Conflict, **AT LEAST ONE** of the requested files had a different status than the others, e.g. one requested file exists, but another was deleted, and a third was never seen. The client needs to perform one request per file id instead of multiple ids in one request, in order to find out the status of each file."),
			ApiResponse(responseCode = "410", description = "File Gone, **ALL** of the requested files have been present in the file storage but have since been deleted.")
		])
	@RequestMapping(
		method = [RequestMethod.HEAD],
		value = ["/files/{ids}"]
	)
	override fun checkFilesByIds(
		@Parameter(description = "A list of ids of files to be checked whether they exist in the file storage.", required = true) @PathVariable("ids") ids: List<String>,
		@Parameter(description = "Tracing id that will be used in logging statements.") @RequestHeader(value = "X-innsendingId", required = false) xInnsendingId: String?
	): ResponseEntity<Unit> {
		logger.info("$xInnsendingId: Will check the status of the files with the following ids: $ids")

		getFilesService.getFiles(xInnsendingId, ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * The following annotations are copied from [FilesApi.deleteFiles].
	 */
	@Operation(
		summary = "Deletes files with given ids.",
		operationId = "deleteFiles",
		description = "Deletes the files with the given ids from the file storage. References to the files will still be kept, indicating that they once existed, but their content will be deleted. If any id provided does not match a file in the file storage, the response will still be 200.")
	@ApiResponses(
		value = [ApiResponse(responseCode = "200", description = "The requested files were deleted.")])
	@RequestMapping(
		method = [RequestMethod.DELETE],
		value = ["/files/{ids}"]
	)
	override fun deleteFiles(
		@Parameter(description = "Given a list of ids (strings), this endpoint returns all files associated with those ids.", required = true) @PathVariable("ids") ids: List<String>,
		@Parameter(description = "Tracing id that will be used in logging statements.") @RequestHeader(value = "X-innsendingId", required = false) xInnsendingId: String?
	): ResponseEntity<Unit> {
		logger.info("$xInnsendingId: Will delete the files with the following ids: $ids")

		deleteFilesService.deleteFiles(xInnsendingId, ids)
		return ResponseEntity(HttpStatus.OK)
	}


	/**
	 * The following annotations are copied from [FilesApi.findFilesByIds].
	 */
	@Operation(
		summary = "Finds files with given ids.",
		operationId = "findFilesByIds",
		description = "Given a list of ids, this endpoint returns all files associated with those ids.")
	@ApiResponses(
		value = [
			ApiResponse(responseCode = "200", description = "Successful operation, **ALL** of the requested files were found and are returned."),
			ApiResponse(responseCode = "404", description = "File Not Found, **NONE** of the requested files have ever been seen in the file storage."),
			ApiResponse(responseCode = "409", description = "Conflict, **AT LEAST ONE** of the requested files had a different status than the others, e.g. one requested file exists, but another was deleted, and a third was never seen. The client needs to perform one request per file id instead of multiple ids in one request, in order to find out the status of each file."),
			ApiResponse(responseCode = "410", description = "File Gone, **ALL** of the requested files have been present in the file storage but have since been deleted.")
		])
	@RequestMapping(
		method = [RequestMethod.GET],
		value = ["/files/{ids}"],
		produces = ["application/json"]
	)
	override fun findFilesByIds(
		@Parameter(description = "A list of ids of files to be retrieved from the file storage.", required = true) @PathVariable("ids") ids: List<String>,
		@Parameter(description = "Tracing id that will be used in logging statements.") @RequestHeader(value = "X-innsendingId", required = false) xInnsendingId: String?
	): ResponseEntity<List<FileData>> {
		logger.info("$xInnsendingId: Will get files with the following ids: $ids")

		return ResponseEntity.ok(getFilesService.getFiles(xInnsendingId, ids))
	}
}
