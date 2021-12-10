package no.nav.soknad.arkivering.soknadsfillager.rest.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.naming.AuthenticationException

import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException

@ControllerAdvice
class ControllerExceptionsHandler {

	private val logger = LoggerFactory.getLogger(javaClass)

	@ExceptionHandler(
		EntityNotFoundException::class,
		NoSuchElementException::class,
		NoResultException::class,
		KotlinNullPointerException::class
	)
	fun notFoundException(e: Exception) = generateErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e)

	@ExceptionHandler(FileGoneException::class)
	fun resourceGoneException(e: Exception) = generateErrorResponse(HttpStatus.GONE, "Resource gone", e)

	@ExceptionHandler(FileNotSeenException::class)
	fun resourceNotSeenException(e: Exception) =
		generateErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e)

	@ExceptionHandler(AuthenticationException::class)
	fun forbiddenException(e: Exception) =
		generateErrorResponse(HttpStatus.UNAUTHORIZED, "You are not allowed to do this operation", e)


	private fun generateErrorResponse(status: HttpStatus, message: String, e: Exception): ResponseEntity<ErrorResponse> {
		logger.warn(e.message)
		return ResponseEntity(ErrorResponse(status, message), status)
	}
}
