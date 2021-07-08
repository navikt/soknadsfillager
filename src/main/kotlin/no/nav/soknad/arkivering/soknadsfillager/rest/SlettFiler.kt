package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.service.SlettFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SlettFiler(private val slettFilerService: SlettFilerService, private val appConfig: AppConfiguration) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@DeleteMapping("/filer")
	fun slettFiler(@RequestParam ids: List<String>) {
		try {
			logger.info("Skal slette '$ids'")

			slettFilerService.slettFiler(ids)

		} catch (e: Exception) {
			appConfig.applicationState.alive = false
			logger.error("Exception occurred!", e)
			throw e
		}
	}
}
