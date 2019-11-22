package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class HentFilerTest {
    @Autowired
    private lateinit var lagreFiler: LagreFiler

    @Autowired
    private lateinit var hentFiler: HentFiler

    @Autowired
    private lateinit var mittRepository: FilRepository

    @AfterEach
    fun ryddOpp() {
        mittRepository.deleteAll()
    }

    @Test
    fun hentEtDokumentTest() {
        val (minUuid, minFil) = opprettEnEnkelFil()

        val minMotattFilIListe =
                opprettMottattFilListeMedBareEnFil(minUuid, minFil)

        this.lagreFiler.lagreFiler(minMotattFilIListe)

        val mittDokumentSomSkalHentes =
                listOf<String>(minUuid)

        hentFiler.hentFiler(mittDokumentSomSkalHentes)

        assertTrue(this.mittRepository.findById(minUuid).isPresent)
    }

    @Test
    fun hentEnListeAvDokumenterTest() {
        val mineFilerListe = opprettListeAv3FilDtoer()

        this.lagreFiler.lagreFiler(mineFilerListe)

        val minUuidListe = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(mineFilerListe)

        assertTrue(this.mittRepository.findById(minUuidListe.first()).isPresent)
        assertTrue(this.mittRepository.findById(minUuidListe.last()).isPresent)

        val listeAvHentedeFiler = hentFiler.hentFiler(minUuidListe)

        assertEquals(minUuidListe, listeAvHentedeFiler.map { it.uuid })
    }

    @Test
    fun hentEnListeavDokumenterHvorIkkeAlleUuiderErKnyttetDokument() {
        val mineFilerListe = opprettListeAv3FilDtoer()

        this.lagreFiler.lagreFiler(mineFilerListe)

        val minUuidListeSomHarDokumenter = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(mineFilerListe)

        val uuid1SomIkkeErBlandtDokumentene = opprettEnUUid()
        val uuid2SomIkkeErBlandtDokumentene = opprettEnUUid()

        val listeSomHarUuidErSomIkkeFinnes: MutableList<String> = endreListtilMutableList(minUuidListeSomHarDokumenter)

        assertEquals(3, listeSomHarUuidErSomIkkeFinnes.size)

        listeSomHarUuidErSomIkkeFinnes.add(uuid1SomIkkeErBlandtDokumentene)
        listeSomHarUuidErSomIkkeFinnes.add(uuid2SomIkkeErBlandtDokumentene)

        assertEquals(5, listeSomHarUuidErSomIkkeFinnes.size)

        listeSomHarUuidErSomIkkeFinnes.shuffle()

        assertTrue(this.mittRepository.findById(minUuidListeSomHarDokumenter.first()).isPresent)
        assertTrue(this.mittRepository.findById(uuid1SomIkkeErBlandtDokumentene).isEmpty)
        assertTrue(hentFiler.hentFiler(listeSomHarUuidErSomIkkeFinnes).size == 5)


    }
}
