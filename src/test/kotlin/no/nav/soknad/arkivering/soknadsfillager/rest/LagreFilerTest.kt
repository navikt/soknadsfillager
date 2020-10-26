package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.Metrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LagreFilerTest {
	val minUuid = opprettEnUUid()
	val mineFilerListe = opprettListeAv3FilDtoer()
	val testFileSize = 625172.0

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var filRepository: FilRepository

	@BeforeEach
	private fun lagreListeAvFiler() = lagreFiler.lagreFiler(mineFilerListe)

	@AfterEach
	fun ryddOpp() = filRepository.deleteAll()

	@Test
	fun enkelTestAvMottaFilerTjenste() {
		val liste = opprettListeMedEnFil(minUuid, opprettEnEnkelPdf())
		val fileCounter = Metrics.filCounterGet(Operations.SAVE.name)
		val errorCounter = Metrics.errorCounterGet(Operations.SAVE.name)

		lagreFiler.lagreFiler(liste)

		assertTrue(filRepository.findById(minUuid).isPresent)
		assertEquals(fileCounter + liste.size.toDouble(), Metrics.filCounterGet(Operations.SAVE.name))
		assertEquals(errorCounter + 0.0, Metrics.errorCounterGet(Operations.SAVE.name))
		assertTrue(Metrics.filSummaryLatencyGet(Operations.SAVE.name).sum > 0 && Metrics.filSummaryLatencyGet(Operations.SAVE.name).count == fileCounter + 1.0)
		assertEquals(testFileSize, (Metrics.filSummarySizeGet(Operations.SAVE.name).sum / Metrics.filSummarySizeGet(Operations.SAVE.name).count))
	}

	@Test
	fun erstatterFilMedGittUuidMedNyFil() {
		val forsteFilVersion = opprettEnEnkelPdf()

		val forsteFilVersjonIliste = opprettListeMedEnFil(minUuid, forsteFilVersion)
		lagreFiler.lagreFiler(forsteFilVersjonIliste)

		assertEquals(forsteFilVersion.size, filRepository.findById(minUuid).get().document?.size)

		val andeFilVersjon = opprettEnEnkelPdf()
		val minAndreFilVersjonIListe = opprettListeMedEnFil(minUuid, andeFilVersjon)
		val fileCounter = Metrics.filCounterGet(Operations.SAVE.name)

		lagreFiler.lagreFiler(minAndreFilVersjonIListe)

		assertEquals(fileCounter + 1.0, Metrics.filCounterGet(Operations.SAVE.name))
		assertEquals(andeFilVersjon.size, filRepository.findById(minUuid).get().document?.size)
	}

	@Test
	fun skalIkkeKunneLagreEnFilDtoSomManglerFil() {
		val antallFilerVedStart = filRepository.count()
		val listeMedFilElementDtoSomManglerFil = opprettListeMedEnFil(minUuid, null)

		lagreFiler.lagreFiler(listeMedFilElementDtoSomManglerFil)

		val filterEtterTest = filRepository.count()
		assertEquals(antallFilerVedStart, filterEtterTest)
	}
}
