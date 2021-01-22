package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SlettFilerService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun slettFiler(filListe: List<String>) {

		filListe.distinct().forEach {
			if (!sjekkOmFilenEksisterer(it)) {
				loggAtFilenMangler(it)
			} else {
				slettFil(it)
			}
		}
	}

	private fun sjekkOmFilenEksisterer(uuid: String) = filRepository.findById(uuid).isPresent

	private fun loggAtFilenMangler(uuid: String) = logger.error("$uuid er ikke i basen")

	private fun slettFil(uuid: String) {
		val fil = filRepository.findById(uuid)

		val oppdatertFil = FilDbData(uuid, null, fil.get().created)

		val start = fileMetrics.filSummaryLatencyStart(Operations.DELETE.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.DELETE.name)
		try {
			filRepository.saveAndFlush(oppdatertFil)
			fileMetrics.filCounterInc(Operations.DELETE.name)

			logger.info("Fil med $uuid er slettet fra basen")
		} catch (error: Exception) {
			fileMetrics.errorCounterInc(Operations.DELETE.name)
			throw error
		} finally {
			fileMetrics.filSummaryLatencyEnd(start)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}
}
