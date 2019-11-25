package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SlettFilerService(private val filRepository: FilRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun slettFiler(filListe: List<String>) {

        filListe.forEach {
            if (!sjekkOmFilenEksisterer(it)) {
                loggAtFilenMangler(it)
            } else {
                slettFil(it)
            }
        }
    }

    private fun sjekkOmFilenEksisterer(uuid: String): Boolean {
        return filRepository.findById(uuid).isPresent
    }

    private fun loggAtFilenMangler(uuid: String) {
        this.logger.error("$uuid er ikke i basen")
    }

    private fun slettFil(uuid: String) {

        logger.info("Fil med $uuid er slettet fra basen")
        filRepository.deleteById(uuid)
    }

}
