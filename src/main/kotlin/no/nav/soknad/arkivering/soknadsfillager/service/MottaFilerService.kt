package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.MottaFilerDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.stereotype.Service

@Service
class MottaFilerService(private val filRepository: FilRepository) {

	fun lagreMottatteFiler(mottaFilerDto: MottaFilerDto){

		val mongoDatabaseObjekt = FilDbData(mottaFilerDto.uuid, mottaFilerDto.melding)
		filRepository.save(mongoDatabaseObjekt)

	}
}
