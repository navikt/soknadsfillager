package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.Metrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SlettFilerService(private val filRepository: FilRepository) {
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

		val start = Metrics.filSummaryLatencyStart(Operations.DELETE.name)
		try {
			filRepository.saveAndFlush(oppdatertFil)
			Metrics.filCounterInc(Operations.DELETE.name)

			logger.info("Fil med $uuid er slettet fra basen")
		} catch (error: Exception) {
			Metrics.errorCounterInc(Operations.DELETE.name)
			throw error
		} finally {
			Metrics.filSummaryLatencyEnd(start)
		}
	}
}
