package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileGoneException
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HentFilerTest {

	private val listeAvFilerIBasen = opprettListeAv3FilDtoer()
	private val listeAvUuiderIBasen = hentUtEnListeAvUuiderFraListeAvFilElementDtoer(listeAvFilerIBasen)
	private val fileSize = 625172.0

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var hentFiler: HentFiler

	@Autowired
	private lateinit var slettFiler: SlettFiler

	@Autowired
	private lateinit var filRepository: FilRepository

	@Autowired
	private lateinit var fileMetrics: FileMetrics

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
		val fileCounter = fileMetrics.filCounterGet(Operations.FIND.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.FIND.name)
		val hentedeFilerResultat = hentFiler.hentFiler(listeAvUuiderIBasen)

		assertEquals(listeAvUuiderIBasen, hentedeFilerResultat.map { it.uuid })
		assertEquals(listeAvFilerIBasen.map { it.fil?.size }, hentedeFilerResultat.map { it.fil?.size })
		assertEquals(fileCounter!! + 3.0, fileMetrics.filCounterGet(Operations.FIND.name)!!)
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.FIND.name)!!)
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0 && fileMetrics.filSummaryLatencyGet(Operations.FIND.name).count >= fileCounter + 3.0)
		assertEquals(fileSize, (fileMetrics.filSummarySizeGet(Operations.FIND.name).sum / fileMetrics.filSummarySizeGet(Operations.FIND.name).count)*1024)
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
		val fileCounter = fileMetrics.filCounterGet(Operations.FIND.name)
		val fileNotFoundCounter = fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name)

		val hentedeFilerResultat = hentFiler.hentFiler(listeSomHarUuidErSomIkkeFinnes)

		assertEquals(5, hentedeFilerResultat.size)
		assertNull(hentedeFilerResultat.find { it.uuid == uuid1SomIkkeErBlandtDokumentene }?.fil)
		assertNotNull(hentedeFilerResultat.find { it.uuid == listeAvUuiderIBasen[0] }?.fil)
		assertEquals(fileCounter!! + 3.0, fileMetrics.filCounterGet(Operations.FIND.name))
		assertEquals(fileNotFoundCounter!! + 2.0, fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name))
	}

	@Test
	fun hentEnListeAvDokumenterHvorEttAvDisseErBlittSlettet() {
		slettFiler.slettFiler(listOf(listeAvUuiderIBasen[0]))

		assertThrows<FileGoneException> {
			hentFiler.hentFiler(listeAvUuiderIBasen)
		}
	}
}
