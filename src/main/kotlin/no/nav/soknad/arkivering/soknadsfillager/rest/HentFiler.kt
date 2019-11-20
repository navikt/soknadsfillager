package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.HentFilerServcie
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HentFiler(private val hentFilerServcie: HentFilerServcie) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@PostMapping ("/hent")
	fun hentDokumenter(@RequestBody filListe:List<String>): List<FilElementDto>{

		return hentFilerServcie.hentFiler(filListe)
	}

}
