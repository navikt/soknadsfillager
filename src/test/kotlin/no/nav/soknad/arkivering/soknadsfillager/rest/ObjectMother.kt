package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import java.util.*
import kotlin.random.Random

internal fun opprettEnUUid(): String {
    val uuid = UUID.randomUUID().toString()

    return uuid
}
internal fun opprettEnTeks(): String {
    val tekst = "min tekst ${opprettEnUUid()}"
    return tekst}



internal fun opprettEnEnkelFil(): Pair<String, String> = Pair(opprettEnUUid(), opprettEnTeks())
internal fun opprettEnFil():FilElementDto = FilElementDto(opprettEnUUid(), opprettEnTeks())

internal fun opprettListeMedEnFil(uuid: String, fil : String): List<FilElementDto> = listOf(FilElementDto(uuid, fil))

internal fun opprettMottattFilListeMedBareEnFil(minUuid: String, minFil: String): List<FilElementDto> = listOf<FilElementDto>(FilElementDto(minUuid, minFil))

internal fun opprettListeAv3FilDtoer(): List<FilElementDto> = listOf<FilElementDto>(opprettEnFil(), opprettEnFil(), opprettEnFil())

internal fun hentUtEnListeAvUuiderFraListeAvFilElementDtoer(list: List<FilElementDto>) = list.map(FilElementDto::uuid)

internal fun endreListtilMutableList(list: List<String>): MutableList<String> = list.toMutableList()


