package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.stereotype.Service
import kotlin.collections.ArrayList

@Service
class HentFilerService(private val filRepository: FilRepository){

	fun hentFiler(filListe:List<String>): List<FilElementDto>{
		return filListe
			.map{e-> hentFil(e)}
			.toCollection(ArrayList())

	}

	fun hentFil( uuid: String): FilElementDto{
		val filDbData = filRepository.findByUuid(uuid)
		return FilElementDto(uuid, filDbData[0]?.data)
	}



}





