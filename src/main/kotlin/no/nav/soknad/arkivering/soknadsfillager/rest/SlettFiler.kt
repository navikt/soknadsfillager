package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.service.SlettFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SlettFiler(private val slettFilerService: SlettFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@DeleteMapping("/filer")
	fun slettFiler(@RequestParam ids: List<String>) {
		logger.info("Forsøker å slette '$ids'")

		slettFilerService.slettFiler(ids)
	}
}
