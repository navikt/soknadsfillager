package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.MottateFilerDto
import no.nav.soknad.arkivering.soknadsfillager.service.LagreFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LeggTilFiler (
private val lagreFilerService: LagreFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/save")
    fun mottaDokumenter(@RequestBody motatteFiler: MottateFilerDto) {
        logger.info("Melding mottatt ${motatteFiler.uuid}, ${motatteFiler.melding}")

			lagreFilerService.lagreMottatteFiler(motatteFiler)
    }

}
