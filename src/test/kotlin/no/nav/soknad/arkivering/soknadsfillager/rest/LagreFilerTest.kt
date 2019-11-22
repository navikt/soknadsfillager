package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
class LagreFilerTest {
    @Autowired
    private lateinit var lagreFiler: LagreFiler

    @Autowired
    private lateinit var mittRepository: FilRepository

    @AfterEach
    fun ryddOpp() {
        mittRepository.deleteAll()
    }

    @Test
    fun enkelTestAvMottaFilerTjenste() {
        val minUuid = opprettEnUUid()
        val liste = opprettListeMedEnFil(minUuid, opprettEnTekstFil())

        this.lagreFiler.lagreFiler(liste)

        assertTrue(this.mittRepository.findById(minUuid).isPresent)
    }

    @Test
    fun lagreEnListeAvFilerOgsjekkRitigAntallLagret() {
        val minListe = opprettListeAv3FilDtoer()

        this.lagreFiler.lagreFiler(minListe)

        assertEquals(3, mittRepository.count().toInt())
    }

    @Test
    fun erstatterFilMedGittUuidMedNyFil(){
        val minUuid = opprettEnUUid()
        val forsteFilVersion = opprettEnTekstFil()

        val minForsteFilVersjonIliste = opprettListeMedEnFil(minUuid, forsteFilVersion)
        this.lagreFiler.lagreFiler(minForsteFilVersjonIliste)

        assertEquals(forsteFilVersion, mittRepository.findById(minUuid).get().data)

        val andeFilVersjon = opprettEnTekstFil()
        val minAndreFilVersjonIListe = opprettListeMedEnFil(minUuid,andeFilVersjon)
        this.lagreFiler.lagreFiler(minAndreFilVersjonIListe)

        assertEquals(andeFilVersjon, mittRepository.findById(minUuid).get().data)
    }
}
