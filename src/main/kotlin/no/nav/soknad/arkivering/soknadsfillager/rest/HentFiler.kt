package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.HentFilerDto
import no.nav.soknad.arkivering.soknadsfillager.service.HentFilerServcie
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HentFiler(private val hentFilerServcie: HentFilerServcie) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@PostMapping
	fun hentDokumenter(@RequestBody hentFiler: HentFilerDto){
		logger.info(("Melding er levert ${hentFiler.uuid}, ${hentFiler.melding}"))

		hentFilerServcie.hentMotatteFiler(hentFiler)
		hentFilerServcie.slettLeverteFiler(hentFiler)
	}

}
