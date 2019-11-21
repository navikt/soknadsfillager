package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.HentFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class HentFiler(private val hentFilerService: HentFilerService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/filer")
    fun hentFiler(@RequestBody filListe: List<String>):
            List<FilElementDto> {
        logger.info("filler hentet ${filListe
                .map { it }}")

        return hentFilerService.hentFiler(filListe)
    }

}
