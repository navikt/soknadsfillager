package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import java.util.*

internal fun opprettEnUUid(): String = UUID.randomUUID().toString()

internal fun opprettEnTekstFil(): String ="min tekst ${opprettEnUUid()}"

internal fun opprettEnEnkelFil(): Pair<String, String> = Pair(opprettEnUUid(), opprettEnTekstFil())
internal fun opprettEnFil():FilElementDto = FilElementDto(opprettEnUUid(), opprettEnTekstFil())

internal fun opprettListeMedEnFil(uuid: String, fil : String): List<FilElementDto> = listOf(FilElementDto(uuid, fil))

internal fun opprettMottattFilListeMedBareEnFil(minUuid: String, minFil: String): List<FilElementDto> = listOf<FilElementDto>(FilElementDto(minUuid, minFil))

internal fun opprettListeAv3FilDtoer(): List<FilElementDto> = listOf<FilElementDto>(opprettEnFil(), opprettEnFil(), opprettEnFil())

internal fun hentUtEnListeAvUuiderFraListeAvFilElementDtoer(list: List<FilElementDto>) = list.map(FilElementDto::uuid)

internal fun endreListtilMutableList(list: List<String>): MutableList<String> = list.toMutableList()


