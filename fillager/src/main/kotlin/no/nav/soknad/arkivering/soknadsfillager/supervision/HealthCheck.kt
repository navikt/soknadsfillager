package no.nav.soknad.arkivering.soknadsfillager.supervision

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/internal"])
class HealthCheck(private val config: AppConfiguration) {

	@GetMapping(value = ["/isAlive"])
	fun isAlive() = if (config.applicationState.ready) "OK" else throw RuntimeException("NOT ALIVE")

	@GetMapping(value = ["/ping"])
	fun ping(): String = "pong"

	@GetMapping(value = ["/isReady"])
	fun isReady() = if (config.applicationState.ready) "Holla, si Ready" else throw RuntimeException("NOT READY")
}
