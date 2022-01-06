package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.service.SlettFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SlettFiler(private val slettFilerService: SlettFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Deprecated("Replaced in favour of OpenAPI generated API code",
		replaceWith = ReplaceWith("RestApi.deleteFiles()"))
	@DeleteMapping("/filer")
	fun slettFiler(@RequestParam ids: List<String>) {
		logger.info("Skal slette '$ids'")

		slettFilerService.slettFiler(ids)
	}
}
