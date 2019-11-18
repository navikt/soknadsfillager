package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.stereotype.Service

@Service
class SlettFilerService(private val filRepository: FilRepository){

	 fun slettFiler (filListe: List<String>){

		 filListe.forEach{slettFil(it)}

	}

	private fun slettFil (uuid: String) {

		filRepository.deleteById(uuid)
	}
}
