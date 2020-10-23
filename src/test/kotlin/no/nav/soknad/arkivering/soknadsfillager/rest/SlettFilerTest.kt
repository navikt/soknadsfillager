package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.Metrics
import no.nav.soknad.arkivering.soknadsfillager.Operations
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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

	@BeforeEach

	@AfterEach
	fun ryddOpp() {
		filRepository.deleteAll()
	}

	@Test
	fun slettFiler() {
		val listeAvMineDokumenterSomSkalSlettes = lagreEnListeAvDokumenter()

		assertEquals(3, filRepository.count())

		val listeAvUuiderSomSkalSlettes = listeAvMineDokumenterSomSkalSlettes.map { it.uuid }
		val fileCounter = Metrics.filCounterGet(Operations.DELETE.name)
		val errorCounter = Metrics.errorCounterGet(Operations.DELETE.name)

		slettFiler.slettFiler(listeAvUuiderSomSkalSlettes)

		assertEquals(3, filRepository.count())

		val filer = hentFiler.hentFiler(listeAvUuiderSomSkalSlettes)
		val empty = filer.stream().filter({ it.fil != null }).toArray()
		assert(empty.isEmpty())
		Assertions.assertTrue(Metrics.filCounterGet(Operations.DELETE.name) == fileCounter + listeAvUuiderSomSkalSlettes.size.toDouble())
		Assertions.assertTrue(Metrics.errorCounterGet(Operations.DELETE.name) == errorCounter + 0.0)
		Assertions.assertTrue(Metrics.filSummaryLatencyGet(Operations.DELETE.name).sum > 0 && Metrics.filSummaryLatencyGet(Operations.DELETE.name).count == fileCounter + listeAvUuiderSomSkalSlettes.size.toDouble())

	}

	@Test
	fun slettFilSomIkkeFinnes() {
		val listeMedFilerSomErLagret = lagreEnListeAvDokumenter()
		val slettelisteMedEkstraUuid = listeMedFilerSomErLagret.map { it.uuid }.toMutableList()

		slettelisteMedEkstraUuid.add(1, UUID.randomUUID().toString())

		slettFiler.slettFiler(slettelisteMedEkstraUuid)

		assertEquals(3, filRepository.count())

		val filer = hentFiler.hentFiler(slettelisteMedEkstraUuid)
		val empty = filer.stream().filter({ it.fil != null }).toArray()
		assert(empty.isEmpty())
	}


	@Test
	fun slettFilerSomLiggerFlerGangerIListen() {

		val listeMedFilerSomErLagret = lagreEnListeAvDokumenter() // A, B, C
		val sletteListe = listeMedFilerSomErLagret.plus(listeMedFilerSomErLagret.first()) // A, B, C, A

		// Databasen vil bare inneholde 3 elementer da element 0 og 3 i sletteListe har samme uuid.
		slettFiler.slettFiler(sletteListe.map { it.uuid })

		assertEquals(3, filRepository.count().toInt())

		val filer = hentFiler.hentFiler(sletteListe.map { it.uuid })
		val empty = filer.stream().filter({ it.fil != null }).toArray()
		assert(empty.isEmpty())
	}

	private fun lagreEnListeAvDokumenter() = opprettListeAv3FilDtoer().also { lagreFiler(it) }

	private fun lagreFiler(list: List<FilElementDto>) = this.lagreFiler.lagreFiler(list)

}
