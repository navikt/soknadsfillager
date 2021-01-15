package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class LagreFilerService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun lagreFiler(filListe: List<FilElementDto>) = filListe.forEach { lagreFil(it) }

	private fun lagreFil(filElementDto: FilElementDto) {

		if (filElementDto.fil == null || filElementDto.fil.isEmpty()) {
			logger.warn("Finnes ingen fil å lagre med Uuid ${filElementDto.uuid}")

		} else {
			val created = filElementDto.opprettet ?: LocalDateTime.now()
			val start = fileMetrics.filSummaryLatencyStart(Operations.SAVE.name)
			try {
				filRepository.save(FilDbData(filElementDto.uuid, filElementDto.fil, created))
				fileMetrics.filCounterInc(Operations.SAVE.name)
				fileMetrics.filSummarySetSize(Operations.SAVE.name, filElementDto.fil.size.toDouble())
				fileMetrics.filHistogramSetSize(Operations.SAVE.name, filElementDto.fil.size.toDouble())
			} catch (error: Exception) {
				fileMetrics.errorCounterInc(Operations.SAVE.name)
				throw error
			} finally {
				fileMetrics.filSummaryLatencyEnd(start)
			}
		}
	}
}
