package no.nav.soknad.arkivering.soknadsfillager.rest


import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.service.LagreFilerService
import org.apache.commons.compress.utils.IOUtils
import java.io.IOException
import java.util.*

internal fun opprettEnUUid(): String = UUID.randomUUID().toString()

internal fun opprettEnEnkelPdf() = getBytesFromFile("/pdf/test.pdf")

internal fun opprettEnFil():FilElementDto = FilElementDto(opprettEnUUid(), opprettEnEnkelPdf())

internal fun opprettListeMedEnFil(uuid: String, fil: ByteArray?): List<FilElementDto> = listOf(FilElementDto(uuid, fil))

internal fun opprettListeAv3FilDtoer(): List<FilElementDto> = listOf<FilElementDto>(opprettEnFil(), opprettEnFil(), opprettEnFil())

internal fun hentUtEnListeAvUuiderFraListeAvFilElementDtoer(list: List<FilElementDto>) = list.map(FilElementDto::uuid)

internal fun opprettEnListeAvFiler(){
    val filliste = listOf<ByteArray>().toMutableList()
    val fil = getBytesFromFile("/pdf/navlogo.pdf")
    val fil2 = getBytesFromFile("/pdf/test.pdf")
    val fil4 = getBytesFromFile("/pdf/test3.pdf")
    filliste.add(fil)
    filliste.add(fil2)
    filliste.add(fil4)
}

@Throws(IOException::class)
fun getBytesFromFile(path: String): ByteArray {
    val resourceAsStream = LagreFilerService::class.java.getResourceAsStream(path)
    return IOUtils.toByteArray(resourceAsStream)
}
