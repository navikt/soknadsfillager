package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.Metrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HentFilerTest {

	private final val listeAvFilerIBasen = opprettListeAv3FilDtoer()
	private val listeAvUuiderIBasen = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(listeAvFilerIBasen)
	private final val fileSize = 625172.0

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var hentFiler: HentFiler

	@Autowired
	private lateinit var filRepository: FilRepository

	@BeforeEach
	private fun lagreListeAvFiler() {
		lagreFiler.lagreFiler(listeAvFilerIBasen)
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
		assertEquals(fileCounter + 3.0, Metrics.filCounterGet(Operations.FIND.name))
		assertEquals(errorCounter + 0.0, Metrics.errorCounterGet(Operations.FIND.name))
		assertTrue(Metrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0 && Metrics.filSummaryLatencyGet(Operations.FIND.name).count >= fileCounter + 3.0)
		assertEquals(fileSize, (Metrics.filSummarySizeGet(Operations.FIND.name).sum / Metrics.filSummarySizeGet(Operations.FIND.name).count))
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

		assertEquals(5, hentedeFilerResultat.size)
		assertNull(hentedeFilerResultat.find { it.uuid == uuid1SomIkkeErBlandtDokumentene }?.fil)
		assertNotNull(hentedeFilerResultat.find { it.uuid == listeAvUuiderIBasen[0] }?.fil)
		assertEquals(fileCounter + 3.0, Metrics.filCounterGet(Operations.FIND.name))
		assertEquals(fileNotFoundCounter + 2.0, Metrics.filCounterGet(Operations.FIND_NOT_FOUND.name))
	}
}
