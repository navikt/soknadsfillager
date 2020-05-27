package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LagreFilerService(private val filRepository: FilRepository) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun lagreFiler(filListe: List<FilElementDto>) = filListe.forEach { this.lagreFil(it) }

	private fun lagreFil(filElementDto: FilElementDto) {

		if (filElementDto.fil == null) {
			logger.warn("Finnes ingen fil å lagre med Uuid ${filElementDto.uuid}")
			return
		} else {
			filRepository.save(FilDbData(filElementDto.uuid, filElementDto.fil, filElementDto.opprettet?: LocalDateTime.now()) )
		}
	}
}
