package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LagreFilerService(private val filRepository: FilRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun lagreFiler(filListe: List<FilElementDto>) = filListe
            .forEach { e -> this.lagreFil(e) }

    private fun lagreFil(filElementDto: FilElementDto) {

        if (filElementDto.fil == null) {
            logger.warn("Forsøker å lagre ${filElementDto.uuid} uten at det er en fil å lagre")
            return
        } else {
            filRepository.save(FilDbData(filElementDto.uuid, filElementDto.fil))
        }
    }
}
