package no.nav.soknad.arkivering.soknadsfillager.internal

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/internal"])
class HealthController(private val config: AppConfiguration) {

	@GetMapping(value = ["/isAlive"])
	fun isAlive(): String = "OK"

	@GetMapping(value = ["/ping"])
	fun ping(): String = "pong"

	@GetMapping(value = ["/isReady"])
	fun isReady(): String = if (config.applicationState.ready) "Holla, si Ready" else throw RuntimeException("NOT READY")
}
