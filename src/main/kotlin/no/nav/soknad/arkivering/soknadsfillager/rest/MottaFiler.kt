package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.MottaFilerDto
import no.nav.soknad.arkivering.soknadsfillager.service.MottaFilerService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MottaFiler (
private val mottaFilerService: MottaFilerService) {
	private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/save")
    fun mottaFiler(@RequestBody mottaFiler: MottaFilerDto) {
        logger.info("Melding mottatt ${mottaFiler.uuid}, ${mottaFiler.melding}")

			mottaFilerService.lagreMottatteFiler(mottaFiler)
    }

}
