package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.MottateFilerDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LagreFilerService(private val filRepository: FilRepository) {

	fun lagreMottatteFiler(mottateFilerDto: MottateFilerDto){

		val mongoDatabaseObjekt = FilDbData(mottateFilerDto.uuid, mottateFilerDto.melding)
		filRepository.save(mongoDatabaseObjekt)

	}
}
