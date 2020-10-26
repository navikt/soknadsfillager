package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.Metrics
import no.nav.soknad.arkivering.soknadsfillager.Operations
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

		if (filElementDto.fil == null || filElementDto.fil.size == 0) {
			logger.warn("Finnes ingen fil å lagre med Uuid ${filElementDto.uuid}")

		} else {
			val created = filElementDto.opprettet ?: LocalDateTime.now()
			val start = Metrics.filSummaryLatencyStart(Operations.SAVE.name)
			try {
				filRepository.save(FilDbData(filElementDto.uuid, filElementDto.fil, created))
				Metrics.filCounterInc(Operations.SAVE.name)
				Metrics.filSummarySetSize(Operations.SAVE.name, filElementDto.fil.size.toDouble())
			} catch (error: Exception) {
				Metrics.errorCounterInc(Operations.SAVE.name)
				throw error
			} finally {
				Metrics.filSummaryLatencyEnd(start)
			}
		}
	}
}
