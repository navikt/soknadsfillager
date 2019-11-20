package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import java.util.*

internal fun opprettEnEnkelFil(): Pair<String, String> {
	val minUuid = UUID.randomUUID().toString()
	val minFil = "Min fil som skal hentes $minUuid"
	return Pair(minUuid, minFil)
}

internal fun opprettMottattFilListeMedBareEnFil(minUuid: String, minFil: String): List<FilElementDto> {
	val minMotattFilIListe =
		listOf<FilElementDto>(FilElementDto(minUuid, minFil))

	return minMotattFilIListe
}

internal fun opprettListeAv3FilDtoer(): List<FilElementDto>{
	val uuid1 = UUID.randomUUID().toString()
	val uuid2 = UUID.randomUUID().toString()
	val uuid3 = UUID.randomUUID().toString()
	val fil1 = "fil$uuid1"
	val fil2 = "fil$uuid2"
	val fil3 = "fil$uuid3"

	val mottattFil1 = FilElementDto(uuid1, fil1)
	val mottattFil2 = FilElementDto(uuid2, fil2)
	val mottaFiler3 = FilElementDto(uuid3, fil3)

	val minListeAvMottatteFiler = listOf<FilElementDto>(mottattFil1, mottattFil2, mottaFiler3)

	return minListeAvMottatteFiler
}

internal fun hentUtenListeAvUuiderFraListeAvFilElementDtoer(list: List<FilElementDto>) = list.map(FilElementDto::uuid)

internal fun endreListtilMutableList(list: List<String>) :MutableList<String> =list.toMutableList()
