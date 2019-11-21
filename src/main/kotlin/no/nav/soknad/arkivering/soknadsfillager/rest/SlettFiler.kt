package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.service.SlettFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SlettFiler(private val slettFilerService: SlettFilerService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @DeleteMapping("/filer")
    fun slettFiler(@RequestBody slettFiler: List<String>) {
        logger.info("Forsøker å slette fler '${slettFiler
                .map { it }}'")

        slettFilerService.slettFiler(slettFiler)
    }

}
