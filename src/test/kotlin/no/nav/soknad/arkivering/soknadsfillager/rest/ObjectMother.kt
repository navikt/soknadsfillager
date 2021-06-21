package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.LagreFilerService
import java.io.ByteArrayOutputStream
import java.util.*

internal fun opprettEnUUid(): String = UUID.randomUUID().toString()

internal fun opprettEnEnkelPdf() = getBytesFromFile("/pdf/test.pdf")

internal fun opprettEnFil(): FilElementDto = FilElementDto(opprettEnUUid(), opprettEnEnkelPdf(), null)

internal fun opprettListeMedEnFil(uuid: String, fil: ByteArray?): List<FilElementDto> = listOf(FilElementDto(uuid, fil, null))

internal fun opprettListeAv3FilDtoer(): List<FilElementDto> = listOf(opprettEnFil(), opprettEnFil(), opprettEnFil())

internal fun hentUtEnListeAvUuiderFraListeAvFilElementDtoer(list: List<FilElementDto>) = list.map(FilElementDto::uuid)


fun getBytesFromFile(path: String): ByteArray {
	val resourceAsStream = LagreFilerService::class.java.getResourceAsStream(path)
	val outputStream = ByteArrayOutputStream()
	resourceAsStream.use { input ->
		outputStream.use { output ->
			input!!.copyTo(output)
		}
	}
	return outputStream.toByteArray()
}
