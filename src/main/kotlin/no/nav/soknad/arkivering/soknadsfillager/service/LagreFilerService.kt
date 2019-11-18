package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.stereotype.Service

@Service
class LagreFilerService(private val filRepository: FilRepository) {

	fun lagreFiler(filListe: List<FilElementDto>) = filListe
			.forEach { e-> this.lagreFil(e) }

	fun lagreFil(mottaFilDto: FilElementDto){

		if (mottaFilDto.fil == null || mottaFilDto.uuid == null ) {
			return
		} else {

			filRepository.save(FilDbData(mottaFilDto.uuid, mottaFilDto.fil))
		}
	}
}
