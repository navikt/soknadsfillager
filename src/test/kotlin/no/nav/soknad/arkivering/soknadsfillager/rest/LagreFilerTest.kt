package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class LagreFilerTest {
    val minUuid = opprettEnUUid()
    val mineFilerListe = opprettListeAv3FilDtoer()
    @Autowired
    private lateinit var lagreFiler: LagreFiler

    @Autowired
    private lateinit var mittRepository: FilRepository

    @BeforeEach
    private fun lagreListeAvFiler()= lagreFiler.lagreFiler(mineFilerListe)

    @AfterEach
    fun ryddOpp() = mittRepository.deleteAll()

    @Test
    fun enkelTestAvMottaFilerTjenste() {
        val liste = opprettListeMedEnFil(minUuid, opprettEnEnkelPdf())

        this.lagreFiler.lagreFiler(liste)

        assertTrue(this.mittRepository.findById(minUuid).isPresent)
    }

    @Test
    fun erstatterFilMedGittUuidMedNyFil(){
        val forsteFilVersion = opprettEnEnkelPdf()

        val forsteFilVersjonIliste = opprettListeMedEnFil(minUuid, forsteFilVersion)
        this.lagreFiler.lagreFiler(forsteFilVersjonIliste)

        assertEquals(forsteFilVersion.size, mittRepository.findById(minUuid).get().data?.size)

        val andeFilVersjon = opprettEnEnkelPdf()
        val minAndreFilVersjonIListe = opprettListeMedEnFil(minUuid,andeFilVersjon)
        this.lagreFiler.lagreFiler(minAndreFilVersjonIListe)

        assertEquals(andeFilVersjon.size, mittRepository.findById(minUuid).get().data?.size)
    }

    @Test
    fun skalIkkeKunneLagreEnFilDtoSomManglerFil(){
        val antallFilerVedStart = mittRepository.count()
        val listeMedFilElementDtoSomManglerFil = opprettListeMedEnFil(minUuid, null)

        this.lagreFiler.lagreFiler(listeMedFilElementDtoSomManglerFil)

        val filterEtterTest = mittRepository.count()
        assertEquals(antallFilerVedStart, filterEtterTest)
    }
}
