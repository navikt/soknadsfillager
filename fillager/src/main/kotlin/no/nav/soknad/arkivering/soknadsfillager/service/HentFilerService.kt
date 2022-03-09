package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.henvendelse.HenvendelseInterface
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileGoneException
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HentFilerService(private val filRepository: FilRepository,
											 private val henvendelse: HenvendelseInterface,
											 appConfig: AppConfiguration,
											 private val fileMetrics: FileMetrics) {

	private val logger = LoggerFactory.getLogger(javaClass)
	private val config = appConfig.restConfig

	fun hentFiler(filListe: List<String>) = filListe.map { hentFil(it) }

	private fun hentFil(uuid: String): FilElementDto {
		val timer = fileMetrics.filSummaryLatencyStart(Operations.FIND.name)
		val histogramTimer = fileMetrics.fileHistogramLatencyStart(Operations.FIND.name)
		try {
			val filDbData = filRepository.findById(uuid)
			return if (!filDbData.isPresent) {
				filElementIkkeFunnet(uuid)
			} else {
				filElementFunnet(filDbData.get())
			}

		} catch (e: Exception) {
			fileMetrics.errorCounterInc(Operations.FIND.name)
			logger.error("Feil ved henting av $uuid", e)
			throw e
		} finally {
			fileMetrics.filSummaryLatencyEnd(timer)
			fileMetrics.fileHistogramLatencyEnd(histogramTimer)
		}
	}

	private fun filElementIkkeFunnet(uuid: String): FilElementDto {
		logger.info("Fil med id='$uuid' finnes ikke i basen")
		if (config.hentFraHenvendelse) {
			val filElementDto = henvendelse.fetchFile(uuid)
			return if (filElementDto == null) {
				fileMetrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
				FilElementDto(uuid, null, null)
			} else {
				fileMetrics.filCounterInc(Operations.FIND_HENVENDELSE.name)
				fileMetrics.filSummarySetSize(Operations.FIND_HENVENDELSE.name, filElementDto.fil?.size?.toDouble())
				fileMetrics.filHistogramSetSize(Operations.FIND_HENVENDELSE.name, filElementDto.fil?.size?.toDouble())
				logger.info("Hentet fil med id='$uuid', size= ${filElementDto.fil?.size} fra Henvendelse")

				val created = filElementDto.opprettet ?: LocalDateTime.now()
				filRepository.save(FilDbData(filElementDto.uuid, filElementDto.fil, created))

				filElementDto
			}
		} else {
			fileMetrics.filCounterInc(Operations.FIND_NOT_FOUND.name)
			return FilElementDto(uuid, null, null)
		}
	}

	private fun filElementFunnet(filDbData: FilDbData): FilElementDto {
		val uuid = filDbData.uuid
		fileMetrics.filCounterInc(Operations.FIND.name)
		fileMetrics.filSummarySetSize(Operations.FIND.name, filDbData.document?.size?.toDouble())
		fileMetrics.filHistogramSetSize(Operations.FIND.name, filDbData.document?.size?.toDouble())
		if (filDbData.document == null) {
			logger.warn("Hentet fil med id='$uuid', size= null. Kaster 410 - GONE")
			throw FileGoneException("Fil med id $uuid er slettet")
		}
		return FilElementDto(uuid, filDbData.document, filDbData.created)
	}
}
