package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
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

		slettFiler.slettFiler(listeAvUuiderSomSkalSlettes)

		assertEquals(0, filRepository.count())
	}

	@Test
	fun slettFilSomIkkeFinnes() {
		val listeMedFilerSomErLagret = lagreEnListeAvDokumenter()
		val slettelisteMedEkstraUuid = listeMedFilerSomErLagret.map { it.uuid }.toMutableList()

		slettelisteMedEkstraUuid.add(1, UUID.randomUUID().toString())

		slettFiler.slettFiler(slettelisteMedEkstraUuid)

		assertEquals(0, filRepository.count())
	}


	@Test
	fun slettFilerSomLiggerFlerGangerIListen() {

		val listeMedFilerSomErLagret = lagreEnListeAvDokumenter() // A, B, C
		val sletteListe = listeMedFilerSomErLagret.plus(listeMedFilerSomErLagret.first()) // A, B, C, A

		slettFiler.slettFiler(sletteListe.map { it.uuid })

		assertEquals(0, filRepository.count().toInt())
	}

	private fun lagreEnListeAvDokumenter() = opprettListeAv3FilDtoer().also { lagreFiler(it) }

	private fun lagreFiler(list: List<FilElementDto>) = this.lagreFiler.lagreFiler(list)

}
