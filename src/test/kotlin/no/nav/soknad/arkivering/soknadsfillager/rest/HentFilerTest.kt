package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.validation.constraints.AssertTrue

@SpringBootTest
class HentFilerTest {
    val mineFilerListe = opprettListeAv3FilDtoer()

    @Autowired
    private lateinit var lagreFiler: LagreFiler

    @Autowired
    private lateinit var hentFiler: HentFiler

    @Autowired
    private lateinit var mittRepository: FilRepository

    @BeforeEach
    private fun lagreListeAvFiler() {
        this.lagreFiler.lagreFiler(mineFilerListe)
    }

    @AfterEach
    fun ryddOpp() {
        mittRepository.deleteAll()
    }


    private fun hentMineFiler(dokumenterSomSkalHentes: List<String>) {
        hentFiler.hentFiler(dokumenterSomSkalHentes)
    }

    @Test
    fun hentEnListeAvDokumenterTest() {

        val minUuidListe = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(mineFilerListe)
        val hentedeFilerResultat = hentFiler.hentFiler(minUuidListe)

        assertEquals(minUuidListe, hentedeFilerResultat.map { it.uuid })
        assertEquals(mineFilerListe.map { it.fil }, hentedeFilerResultat.map { it.fil })
    }

    @Test
    fun hentEnListeavDokumenterHvorIkkeAlleUuiderErKnyttetDokument() {
        val minUuidListeSomHarDokumenter = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(mineFilerListe)
        val uuid1SomIkkeErBlandtDokumentene = opprettEnUUid()
        val uuid2SomIkkeErBlandtDokumentene = opprettEnUUid()
        val listeSomHarUuidErSomIkkeFinnes: MutableList<String> = endreListtilMutableList(minUuidListeSomHarDokumenter)

        assertEquals(3, listeSomHarUuidErSomIkkeFinnes.size)

        listeSomHarUuidErSomIkkeFinnes.add(uuid1SomIkkeErBlandtDokumentene)
        listeSomHarUuidErSomIkkeFinnes.add(uuid2SomIkkeErBlandtDokumentene)
        assertEquals(5, listeSomHarUuidErSomIkkeFinnes.size)

        val hentedeFilerResultat = hentFiler.hentFiler(listeSomHarUuidErSomIkkeFinnes)
        assertTrue(hentedeFilerResultat.size == 5)
        assertTrue(hentedeFilerResultat.find { it.uuid == uuid1SomIkkeErBlandtDokumentene}?.fil == null)
        assertTrue(hentedeFilerResultat.find{ it.uuid == minUuidListeSomHarDokumenter.get(0)}?.fil != null)
    }
}
