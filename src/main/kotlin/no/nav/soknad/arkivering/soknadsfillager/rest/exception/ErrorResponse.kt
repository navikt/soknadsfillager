package no.nav.soknad.arkivering.soknadsfillager.rest.exception

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ErrorResponse(
	status: HttpStatus,
	message_: String
) {

	val code: Int
	val state: String
	val message: String

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	val timestamp: LocalDateTime

	init {
		code = status.value()
		state = status.name
		message = message_
		timestamp = LocalDateTime.now()
	}
}
