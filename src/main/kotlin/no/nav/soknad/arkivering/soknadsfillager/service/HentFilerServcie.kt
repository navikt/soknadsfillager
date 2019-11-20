package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.collections.ArrayList

@Service
class HentFilerService(private val filRepository: FilRepository){
	private val logger = LoggerFactory.getLogger(javaClass)

	fun hentFiler(filListe:List<String>): List<FilElementDto>{
		return filListe
			.map{e-> hentFil(e)}
			.toCollection(ArrayList())

	}

	fun hentFil( uuid: String): FilElementDto{
		try {
			val filDbData = filRepository.findByUuid(uuid)
			if (filDbData.size == 0) {
				logger.info("$uuid er finnes ikke i basen")
				return FilElementDto(uuid, null)
			} else if (filDbData.size == 1) {
				return FilElementDto(uuid, filDbData[0]?.data)
			} else {
				logger.warn("Det er flere innslag for $uuid")
				return FilElementDto(uuid, filDbData[0]?.data)
			}

		} catch (e:Exception){
			logger.error("$uuid er ikke i basen", e)
			throw e
		}
	}



}





