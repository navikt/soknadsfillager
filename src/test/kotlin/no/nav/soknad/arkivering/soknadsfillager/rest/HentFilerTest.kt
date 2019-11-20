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
	fun ryddOpp(){
		mittRepository.deleteAll()
	}

	@Test
    fun hentEtDokumentTest() {
		val (minUuid, minFil) = opprettEnEnkelFil()

		val minMotattFilIListe =
			opprettMottattFilListeMedBareEnFil(minUuid, minFil)

		this.lagreFiler.mottaFiler(minMotattFilIListe)

			val mittDokumentSomSkalHentes =
				listOf<String>(minUuid)

				hentFiler.hentDokumenter(mittDokumentSomSkalHentes)

		assertTrue(this.mittRepository.findByUuid(minUuid).isNotEmpty())
    }

	@Test
	fun  hentEnListeAvDokumenterTest(){
		val mineFilerListe = opprett3Filer()

		this.lagreFiler.mottaFiler(mineFilerListe)

		val minUuidListe=  hentUtenListeAvUuiderFraListeAvFilElementDtoer(mineFilerListe)

		assertTrue(this.mittRepository.findByUuid(minUuidListe.first()).isNotEmpty())
		assertTrue(this.mittRepository.findById(minUuidListe.last()).isPresent)
	}

	@Test
	fun hentEnListeavDokumenterHvorIkkeAlleUuiderErKnyttetDokument(){
		val mineFilerListe = opprett3Filer()

		this.lagreFiler.mottaFiler(mineFilerListe)

		val minUuidListeSomHarDokumenter=  hentUtenListeAvUuiderFraListeAvFilElementDtoer(mineFilerListe)

		val uuid1SomIkkeErBlandtDokumentene = UUID.randomUUID().toString()
		val uuid2SomIkkeErBlandtDokumentene = UUID.randomUUID().toString()

		val listeSomHarUuidErSomIkkeFinnes: MutableList<String> = endreListtilMutableList(minUuidListeSomHarDokumenter)

		assertEquals(3, listeSomHarUuidErSomIkkeFinnes.size)

		listeSomHarUuidErSomIkkeFinnes.add(uuid1SomIkkeErBlandtDokumentene)
		listeSomHarUuidErSomIkkeFinnes.add(uuid2SomIkkeErBlandtDokumentene)

		assertEquals(5, listeSomHarUuidErSomIkkeFinnes.size)

		listeSomHarUuidErSomIkkeFinnes.random()

		assertTrue(this.mittRepository.findByUuid(minUuidListeSomHarDokumenter.first()).isNotEmpty())
		assertTrue(this.mittRepository.findByUuid(uuid1SomIkkeErBlandtDokumentene).isEmpty())
		assertTrue(hentFiler.hentDokumenter(listeSomHarUuidErSomIkkeFinnes).size == 5)




	}
}
