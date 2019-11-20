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

