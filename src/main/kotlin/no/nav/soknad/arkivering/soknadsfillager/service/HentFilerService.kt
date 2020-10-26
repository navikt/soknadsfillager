package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.henvendelse.HenvendelseInterface
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.Metrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HentFilerService(private val filRepository: FilRepository,
											 private val henvendelse: HenvendelseInterface,
											 appConfig: AppConfiguration) {

	private val logger = LoggerFactory.getLogger(javaClass)
	private val config = appConfig.restConfig

	fun hentFiler(filListe: List<String>) = filListe.map { hentFil(it) }

	private fun hentFil(uuid: String): FilElementDto {
		val timer = Metrics.filSummaryLatencyStart(Operations.FIND.name)
		try {
			val filDbData = filRepository.findById(uuid)
			return if (!filDbData.isPresent) {
				logger.info("Fil med id='$uuid' finnes ikke i basen")
				if (config.hentFraHenvendelse) {
					val filElementDto = henvendelse.fetchFile(uuid)
					return if (filElementDto == null) {
						Metrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
						FilElementDto(uuid, null, null)
					} else {
						Metrics.filCounterInc(Operations.FIND_HENVENDELSE.name)
						Metrics.filSummarySetSize(Operations.FIND_HENVENDELSE.name, filElementDto.fil?.size?.toDouble())
						logger.info("Hentet fil med id='$uuid', size= ${filElementDto.fil?.size}  fra Henvendelse")
						filElementDto
					}
				} else {
					Metrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
					FilElementDto(uuid, null, null)
				}
			} else {
				Metrics.filCounterInc(Operations.FIND.name)
				Metrics.filSummarySetSize(Operations.FIND.name, filDbData.get().document?.size?.toDouble())
				return FilElementDto(uuid, filDbData.get().document, filDbData.get().created)
			}

		} catch (e: Exception) {
			Metrics.errorCounterInc(Operations.FIND.name)
			logger.error("Feil ved henting av $uuid", e)
			throw e
		} finally {
			Metrics.filSummaryLatencyEnd(timer)
		}
	}
}
