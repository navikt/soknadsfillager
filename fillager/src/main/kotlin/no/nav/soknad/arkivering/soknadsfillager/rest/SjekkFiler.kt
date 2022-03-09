package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.HentFilerService
import no.nav.soknad.arkivering.soknadsfillager.service.SjekkFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SjekkFiler(private val sjekkFilerService: SjekkFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Deprecated("Replaced in favour of OpenAPI generated API code",
		replaceWith = ReplaceWith("RestApi.checkFilesByIds()"))
	@GetMapping("/filesExist")
	fun sjekkFiler(@RequestParam ids: List<String>) {
		logger.info("Skal sjekke hvis disse filer finnes: $ids")

		sjekkFilerService.sjekkFiler(ids)

		logger.info("Alle filer finnes: $ids")
	}
}
