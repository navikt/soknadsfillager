package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.dto.HentFilerDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.stereotype.Service

@Service
class HentFilerServcie(private val filRepository: FilRepository){

	fun hentMotatteFiler(hentFilerDto: HentFilerDto){
		val mongoDatabaseObjekt = FilDbData(hentFilerDto.uuid, hentFilerDto.melding)
		filRepository.findByUuid(mongoDatabaseObjekt)
	}

	fun slettLeverteFiler (hentFilerDto: HentFilerDto){
		val mongoDatabaseObjekt = FilDbData(hentFilerDto.uuid, hentFilerDto.melding)
		filRepository.delete(mongoDatabaseObjekt)
	}
}
