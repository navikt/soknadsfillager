package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.Metrics
import no.nav.soknad.arkivering.soknadsfillager.Operations
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HentFilerTest {

	private final val listeAvFilerIBasen = opprettListeAv3FilDtoer()
	val listeAvUuiderIBasen = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(listeAvFilerIBasen)

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var hentFiler: HentFiler

	@Autowired
	private lateinit var filRepository: FilRepository

	@BeforeEach
	private fun lagreListeAvFiler() {
		this.lagreFiler.lagreFiler(listeAvFilerIBasen)
	}

	@AfterEach
	fun ryddOpp() {
		filRepository.deleteAll()
	}

	@Test
	fun hentEnListeAvDokumenterTest() {
		val fileCounter = Metrics.filCounterGet(Operations.FIND.name)
		val errorCounter = Metrics.errorCounterGet(Operations.FIND.name)
		val hentedeFilerResultat = hentFiler.hentFiler(listeAvUuiderIBasen)

		assertEquals(listeAvUuiderIBasen, hentedeFilerResultat.map { it.uuid })
		assertEquals(listeAvFilerIBasen.map { it.fil?.size }, hentedeFilerResultat.map { it.fil?.size })
		assertTrue(Metrics.filCounterGet(Operations.FIND.name) == fileCounter + 3.0)
		assertTrue(Metrics.errorCounterGet(Operations.FIND.name) == errorCounter + 0.0)
		assertTrue(Metrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0 && Metrics.filSummaryLatencyGet(Operations.FIND.name).count == fileCounter + 3.0)
		assertTrue((Metrics.filSummarySizeGet(Operations.FIND.name).sum/Metrics.filSummarySizeGet(Operations.FIND.name).count) == 625172.0)
	}

	@Test
	fun hentEnListeAvDokumenterHvorIkkeAlleUuiderErKnyttetTilFil() {
		val uuid1SomIkkeErBlandtDokumentene = opprettEnUUid()
		val uuid2SomIkkeErBlandtDokumentene = opprettEnUUid()
		val listeSomHarUuidErSomIkkeFinnes: MutableList<String> = listeAvUuiderIBasen.toMutableList()

		assertEquals(3, listeSomHarUuidErSomIkkeFinnes.size)

		listeSomHarUuidErSomIkkeFinnes.add(uuid1SomIkkeErBlandtDokumentene)
		listeSomHarUuidErSomIkkeFinnes.add(uuid2SomIkkeErBlandtDokumentene)
		assertEquals(5, listeSomHarUuidErSomIkkeFinnes.size)
		val fileCounter = Metrics.filCounterGet(Operations.FIND.name)
		val fileNotFoundCounter = Metrics.filCounterGet(Operations.FIND_NOT_FOUND.name)

		val hentedeFilerResultat = hentFiler.hentFiler(listeSomHarUuidErSomIkkeFinnes)
		assertTrue(hentedeFilerResultat.size == 5)
		assertTrue(hentedeFilerResultat.find { it.uuid == uuid1SomIkkeErBlandtDokumentene }?.fil == null)
		assertTrue(hentedeFilerResultat.find { it.uuid == listeAvUuiderIBasen[0] }?.fil != null)
		assertTrue(Metrics.filCounterGet(Operations.FIND.name) == fileCounter + 3.0)
		assertTrue(Metrics.filCounterGet(Operations.FIND_NOT_FOUND.name) == fileNotFoundCounter + 2.0)
	}

	@Test
	fun hentEnListeAvFilerFlereGangerTest() {

		hentFiler()
		System.out.println("Ferdig1")

		Thread.sleep(6100)

		hentFiler()
		System.out.println("Ferdig2")

		Thread.sleep(6100)
		hentFiler()
		System.out.println("Ferdig3")

		System.out.println("FerdigFerdig")

	}

	private fun hentFiler() {
		val hentedeFilerResultat = hentFiler.hentFiler(listeAvUuiderIBasen)

		assertEquals(listeAvUuiderIBasen, hentedeFilerResultat.map { it.uuid })
		assertEquals(listeAvFilerIBasen.map { it.fil?.size }, hentedeFilerResultat.map { it.fil?.size })

	}

}
