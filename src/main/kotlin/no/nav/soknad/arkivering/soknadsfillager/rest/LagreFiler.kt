package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.LagreFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LagreFiler(private val lagreFilerService: LagreFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@PostMapping("/filer")
	fun lagreFiler(@RequestBody filer: List<FilElementDto>) {
		logger.info("Skal lagre filer '${filer.map { "id: '" + it.uuid + "', size in bytes: " + it.fil?.size }}'")

		lagreFilerService.lagreFiler(filer)
	}
}
