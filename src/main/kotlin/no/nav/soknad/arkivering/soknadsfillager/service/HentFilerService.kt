package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.henvendelse.HenvendelseInterface
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class HentFilerService(private val filRepository: FilRepository, private val henvendelse: HenvendelseInterface, private val appConfig: AppConfiguration) {
	private val logger = LoggerFactory.getLogger(HentFilerService::class.java)
	private val config = 	appConfig.restConfig

	fun hentFiler(filListe: List<String>) = filListe.map { hentFil(it) }

	private fun hentFil(uuid: String): FilElementDto {
		try {
			val filDbData = filRepository.findById(uuid)
			return if (!filDbData.isPresent) {
				this.logger.info("Fil med id='$uuid' finnes ikke i basen")
				if (config.hentFraHenvendelse) {
					FilElementDto(uuid, henvendelse.fetchFile(uuid), null)
				} else {
					FilElementDto(uuid, null, null)
				}
			} else {
				FilElementDto(uuid, filDbData.get().data, filDbData.get().opprettet)
			}

		} catch (e: Exception) {
			logger.error("Feil ved henting av $uuid", e)
			throw e
		}
	}
}
