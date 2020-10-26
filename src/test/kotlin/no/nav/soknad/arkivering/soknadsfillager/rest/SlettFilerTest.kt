package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.Metrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations.DELETE
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class SlettFilerTest {
	@Autowired
	private lateinit var slettFiler: SlettFiler

	@Autowired
	private lateinit var filRepository: FilRepository

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var hentFiler: HentFiler

	@AfterEach
	fun ryddOpp() {
		filRepository.deleteAll()
	}

	@Test
	fun slettFiler() {
		val listeAvMineDokumenterSomSkalSlettes = lagreEnListeAvDokumenter()

		assertEquals(3, filRepository.count())

		val listeAvUuiderSomSkalSlettes = listeAvMineDokumenterSomSkalSlettes.map { it.uuid }
		val fileCounter = Metrics.filCounterGet(DELETE.name)
		val errorCounter = Metrics.errorCounterGet(DELETE.name)

		slettFiler.slettFiler(listeAvUuiderSomSkalSlettes)

		assertEquals(3, filRepository.count())

		val filer = hentFiler.hentFiler(listeAvUuiderSomSkalSlettes)
		val nonNullFiles = filer.stream().filter { it.fil != null }.toArray()
		assertTrue(nonNullFiles.isEmpty())
		assertEquals(fileCounter + listeAvUuiderSomSkalSlettes.size.toDouble(), Metrics.filCounterGet(DELETE.name))
		assertEquals(errorCounter + 0.0, Metrics.errorCounterGet(DELETE.name))
		assertTrue(Metrics.filSummaryLatencyGet(DELETE.name).sum > 0 && Metrics.filSummaryLatencyGet(DELETE.name).count == fileCounter + listeAvUuiderSomSkalSlettes.size.toDouble())
	}

	@Test
	fun slettFilSomIkkeFinnes() {
		val listeMedFilerSomErLagret = lagreEnListeAvDokumenter()
		val slettelisteMedEkstraUuid = listeMedFilerSomErLagret.map { it.uuid }.toMutableList()

		slettelisteMedEkstraUuid.add(1, UUID.randomUUID().toString())

		slettFiler.slettFiler(slettelisteMedEkstraUuid)

		assertEquals(3, filRepository.count())

		val filer = hentFiler.hentFiler(slettelisteMedEkstraUuid)
		val nonNullFiles = filer.stream().filter { it.fil != null }.toArray()
		assertTrue(nonNullFiles.isEmpty())
	}


	@Test
	fun slettFilerSomLiggerFlerGangerIListen() {

		val listeMedFilerSomErLagret = lagreEnListeAvDokumenter() // A, B, C
		val sletteListe = listeMedFilerSomErLagret.plus(listeMedFilerSomErLagret.first()) // A, B, C, A

		// Databasen vil bare inneholde 3 elementer da element 0 og 3 i sletteListe har samme uuid.
		slettFiler.slettFiler(sletteListe.map { it.uuid })

		assertEquals(3, filRepository.count().toInt())

		val filer = hentFiler.hentFiler(sletteListe.map { it.uuid })
		val empty = filer.stream().filter { it.fil != null }.toArray()
		assert(empty.isEmpty())
	}

	private fun lagreEnListeAvDokumenter() = opprettListeAv3FilDtoer().also { lagreFiler(it) }

	private fun lagreFiler(list: List<FilElementDto>) = lagreFiler.lagreFiler(list)
}
