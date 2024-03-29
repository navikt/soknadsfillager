package no.nav.soknad.arkivering.soknadsfillager.rest.exception

import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.NoResultException
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

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

	@ExceptionHandler(ConflictException::class)
	fun conflictException(e: Exception) =
		generateErrorResponse(HttpStatus.CONFLICT, "Requested ids had many different statuses", e)

	@ExceptionHandler(FileNotSeenException::class)
	fun resourceNotSeenException(e: Exception) =
		generateErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e)

	@ExceptionHandler(value = [JwtTokenMissingException::class, JwtTokenUnauthorizedException::class])
	fun forbiddenException(e: Exception) =
		generateErrorResponse(HttpStatus.UNAUTHORIZED, "You are not allowed to do this operation", e)

	@ExceptionHandler(EmptyContentException::class)
	fun emptyContentException(e: Exception) =
		generateErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "${e.message}", e)


	private fun generateErrorResponse(status: HttpStatus, message: String, e: Exception): ResponseEntity<ErrorResponse> {
		logger.warn(e.message)
		return ResponseEntity(ErrorResponse(status, message), status)
	}
}
