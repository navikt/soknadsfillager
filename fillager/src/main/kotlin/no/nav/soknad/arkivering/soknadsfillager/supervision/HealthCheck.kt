package no.nav.soknad.arkivering.soknadsfillager.supervision

import no.nav.security.token.support.core.api.Unprotected
import no.nav.soknad.arkivering.soknadsfillager.api.HealthApi
import no.nav.soknad.arkivering.soknadsfillager.model.ApplicationStatus
import no.nav.soknad.arkivering.soknadsfillager.model.ApplicationStatusType
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class HealthCheck(
	private val filRepository: FilRepository, @Value("\${status_log_url}") private val statusLogUrl: String
) : HealthApi {

	private val logger = LoggerFactory.getLogger(javaClass)

	@Unprotected
	override fun isAlive(): ResponseEntity<Unit> =
		if (checkDatabase()) ResponseEntity(HttpStatus.OK) else ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)

	@Unprotected
	override fun ping() = ResponseEntity<Unit>(HttpStatus.OK)

	@Unprotected
	override fun isReady(): ResponseEntity<Unit> =
		if (checkDatabase()) ResponseEntity(HttpStatus.OK) else ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE)

	private fun checkDatabase(): Boolean {
		return try {
			logger.debug("Checking that the database is available")
			filRepository.documentCount() >= 0
		} catch (e: Exception) {
			logger.warn("The database check failed with ${e.message}", e)
			false
		}
	}

	@Unprotected
	override fun getStatus(): ResponseEntity<ApplicationStatus> {
		return if (checkDatabase()) {
			ResponseEntity(
				ApplicationStatus(status = ApplicationStatusType.OK, description = "OK", logLink = statusLogUrl),
				HttpStatus.OK
			)
		} else {
			ResponseEntity(
				ApplicationStatus(
					status = ApplicationStatusType.DOWN,
					description = "Database is not responding",
					logLink = statusLogUrl
				),
				HttpStatus.OK
			)
		}

	}
}
