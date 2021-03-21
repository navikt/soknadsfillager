package no.nav.soknad.arkivering.soknadsfillager.rest.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
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
		//EmptyResultDataAccessException::class,
		KotlinNullPointerException::class
	)
	fun notFoundException(e: Exception): ResponseEntity<ErrorResponse> {
		return generateErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e)
	}

	@ExceptionHandler(
		EmptyResultDataAccessException::class
	)
	fun resourceGoneException(e: Exception): ResponseEntity<ErrorResponse> {
		return generateErrorResponse(HttpStatus.GONE, "Resource gone", e)
	}

/*
	@ExceptionHandler(AuthorizationException::class)
	fun unauthorizedException(e: Exception): ResponseEntity<ErrorResponse> {
		return generateErrorResponse(HttpStatus.FORBIDDEN, "You are not authorized to do this operation", e)
	}
*/

	@ExceptionHandler(AuthenticationException::class)
	fun forbiddenException(e: Exception): ResponseEntity<ErrorResponse> {
		return generateErrorResponse(HttpStatus.UNAUTHORIZED, "You are not allowed to do this operation", e)
	}

	private fun generateErrorResponse(
		status: HttpStatus,
		message: String,
		e: Exception
	): ResponseEntity<ErrorResponse> {
		logger.warn(e.message)
		return ResponseEntity(ErrorResponse(status, message), status)
	}

}
