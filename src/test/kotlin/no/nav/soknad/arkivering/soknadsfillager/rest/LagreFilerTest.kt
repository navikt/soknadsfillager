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
class LagreFilerTest {
	val minUuid = opprettEnUUid()
	val mineFilerListe = opprettListeAv3FilDtoer()
	val testFileSize = 625172.0

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@BeforeEach
	private fun lagreListeAvFiler() = lagreFiler.lagreFiler(mineFilerListe)

	@AfterEach
	fun ryddOpp() = mittRepository.deleteAll()

	@Test
	fun enkelTestAvMottaFilerTjenste() {
		val liste = opprettListeMedEnFil(minUuid, opprettEnEnkelPdf())
		val fileCounter = Metrics.filCounterGet(Operations.SAVE.name)
		val errorCounter = Metrics.errorCounterGet(Operations.SAVE.name)

		this.lagreFiler.lagreFiler(liste)

		assertTrue(this.mittRepository.findById(minUuid).isPresent)
		assertTrue(Metrics.filCounterGet(Operations.SAVE.name) == fileCounter + liste.size.toDouble())
		assertTrue(Metrics.errorCounterGet(Operations.SAVE.name) == errorCounter + 0.0)
		assertTrue(Metrics.filSummaryLatencyGet(Operations.SAVE.name).sum > 0 && Metrics.filSummaryLatencyGet(Operations.SAVE.name).count == fileCounter + 1.0)
		assertTrue((Metrics.filSummarySizeGet(Operations.SAVE.name).sum/ Metrics.filSummarySizeGet(Operations.SAVE.name).count) == testFileSize)
	}

	@Test
	fun erstatterFilMedGittUuidMedNyFil() {
		val forsteFilVersion = opprettEnEnkelPdf()

		val forsteFilVersjonIliste = opprettListeMedEnFil(minUuid, forsteFilVersion)
		this.lagreFiler.lagreFiler(forsteFilVersjonIliste)

		assertEquals(forsteFilVersion.size, mittRepository.findById(minUuid).get().document?.size)

		val andeFilVersjon = opprettEnEnkelPdf()
		val minAndreFilVersjonIListe = opprettListeMedEnFil(minUuid, andeFilVersjon)
		val fileCounter = Metrics.filCounterGet(Operations.SAVE.name)

		this.lagreFiler.lagreFiler(minAndreFilVersjonIListe)

		assertTrue(Metrics.filCounterGet(Operations.SAVE.name) == fileCounter + 1.0)
		assertEquals(andeFilVersjon.size, mittRepository.findById(minUuid).get().document?.size)
	}

	@Test
	fun skalIkkeKunneLagreEnFilDtoSomManglerFil() {
		val antallFilerVedStart = mittRepository.count()
		val listeMedFilElementDtoSomManglerFil = opprettListeMedEnFil(minUuid, null)

		this.lagreFiler.lagreFiler(listeMedFilElementDtoSomManglerFil)

		val filterEtterTest = mittRepository.count()
		assertEquals(antallFilerVedStart, filterEtterTest)
	}
}
