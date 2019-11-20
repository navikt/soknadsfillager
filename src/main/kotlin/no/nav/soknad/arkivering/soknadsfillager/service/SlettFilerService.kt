package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.RuntimeException

@Service
class SlettFilerService(private val filRepository: FilRepository){
	private val logger = LoggerFactory.getLogger(javaClass)

	 fun slettFiler (filListe: List<String>){

		 filListe.forEach{slettFil(it)}
	}

	private fun slettFil (uuid: String) {
		try {
			if (filRepository.findById(uuid).isEmpty){
				logger.error("$uuid er ikke i basen")
				throw RuntimeException("Fil med $uuid er ikke i basen" )
			}
			logger.info("Fil med $uuid er slettet fra basen")
			filRepository.deleteById(uuid)
		} catch (e:Exception){
			logger.error("Feil tilknyttet fil med $uuid ", e)
			throw e
		}
	}
}
