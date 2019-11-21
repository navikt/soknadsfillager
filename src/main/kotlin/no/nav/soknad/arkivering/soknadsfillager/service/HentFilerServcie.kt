package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HentFilerService(private val filRepository: FilRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun hentFiler(filListe: List<String>): List<FilElementDto> {
        return filListe
                .map { e -> hentFil(e) }
                .toCollection(ArrayList())

    }

    fun hentFil(uuid: String): FilElementDto {
        try {
            val filDbData = filRepository.findByUuid(uuid)
            return if (filDbData.isEmpty()) {
                this.logger.info(" Fil med $uuid er finnes ikke i basen")
                FilElementDto(uuid, null)
            } else {
                if (filDbData.size == 1) {
                    FilElementDto(uuid, filDbData[0].data)
                } else {
                    this.logger.warn("Det er flere innslag for $uuid")
                    FilElementDto(uuid, filDbData[0].data)
                }
            }

        } catch (e: Exception) {
            logger.error("$uuid er ikke i basen", e)
            throw e
        }
    }


}





