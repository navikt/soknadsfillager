package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.HentFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HentFiler(private val hentFilerService: HentFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Deprecated("Replaced in favour of OpenAPI generated API code",
		replaceWith = ReplaceWith("RestApi.findFilesByIds()"))
	@GetMapping("/filer")
	fun hentFiler(@RequestParam ids: List<String>): List<FilElementDto> {
		logger.info("Skal hente følgende filer: $ids")

		val files = hentFilerService.hentFiler(ids)
		logger.info("Hentet filer: '${files.map { "id: '" + it.uuid + "', size in bytes: " + it.fil?.size }}'")
		return files
	}
}
