package no.nav.soknad.arkivering.soknadsfillager.internal

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/internal"])
class HealthController() {


	@GetMapping(value = ["/isAlive"])
	fun isAlive(): String ="OK"

	@GetMapping(value = ["/ping"])
	fun ping(): String = "pong"

	@GetMapping(value = ["/isReady"])
	fun isReady(): String = "Holla, si Ready"
}

