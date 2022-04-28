package no.nav.soknad.arkivering.soknadsfillager.supervision

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.soknad.arkivering.soknadsfillager.api.HealthApi
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheck(private val filRepository: FilRepository) : HealthApi {

	private val logger = LoggerFactory.getLogger(javaClass)

	/**
	 * The following annotations are copied from [HealthApi.isAlive].
	 */
	@Operation(
		summary = "Checks if the application and its dependencies up",
		description = "Checks if the application and its dependencies are up and running.")
	@ApiResponses(
		value = [ApiResponse(responseCode = "200", description = "Successful operation; application is alive"), ApiResponse(responseCode = "500", description = "The application or one of its dependencies are not up and running.")])
	@RequestMapping(
		method = [RequestMethod.GET],
		value = ["/health/isAlive"]
	)
	override fun isAlive(): ResponseEntity<Unit> =
		if (checkDatabase()) ResponseEntity(HttpStatus.OK) else ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)

	/**
	 * The following annotations are copied from [HealthApi.ping].
	 */
	@Operation(
		summary = "Pings the application to see if it responds",
		description = "Pings the application to see if it responds")
	@ApiResponses(
		value = [ApiResponse(responseCode = "200", description = "Successful operation; application is responding")])
	@RequestMapping(
		method = [RequestMethod.GET],
		value = ["/health/ping"]
	)
	override fun ping() = ResponseEntity<Unit>(HttpStatus.OK)

	/**
	 * The following annotations are copied from [HealthApi.isReady].
	 */
	@Operation(
		summary = "Checks if the application is ready to accept traffic",
		description = "Checks if the application is ready to accept traffic.")
	@ApiResponses(
		value = [ApiResponse(responseCode = "200", description = "Successful operation; application is ready"),ApiResponse(responseCode = "503", description = "The application or one of its dependencies are not ready")])
	@RequestMapping(
		method = [RequestMethod.GET],
		value = ["/health/isReady"]
	)
	override fun isReady(): ResponseEntity<Unit> =
		if (checkDatabase()) ResponseEntity(HttpStatus.OK) else ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE)

	private fun checkDatabase(): Boolean {
		try {
			return filRepository.documentCount() >= 0
		} catch (e: Exception) {
			logger.warn("CheckDatabase feilet med ${e.message}")
			return false
		}
	}
}
