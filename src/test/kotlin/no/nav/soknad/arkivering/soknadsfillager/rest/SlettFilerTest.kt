package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
class SlettFilerTest {
	@Autowired
	private lateinit var slettFiler: SlettFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@AfterEach
	fun ryddOpp(){
		mittRepository.deleteAll()
	}

	@Test
	fun slettFilerTest(){

		val listeAvMineDokumenterSomSkalSlettes = opprettListeAv3FilDtoer()
		lagreFiler.lagreFiler(listeAvMineDokumenterSomSkalSlettes)

		val listeAvMineUuiderSomSkalSlettes =
				hentUtenListeAvUuiderFraListeAvFilElementDtoer(listeAvMineDokumenterSomSkalSlettes)

		assertEquals(3, mittRepository.count().toInt())

		slettFiler.slettFiler(listeAvMineUuiderSomSkalSlettes)

		assertEquals(0, mittRepository.count().toInt())
	}

	@Test
	fun slettFilSomIkkeFinnesTest(){
		val minListeMedFilerSomErLagret = opprettListeAv3FilDtoer()
		this.lagreFiler.lagreFiler(minListeMedFilerSomErLagret)

		assertEquals(3, mittRepository.count().toInt())

		val listeAvUuiderSomSkalSlettes =
				endreListtilMutableList(
				hentUtenListeAvUuiderFraListeAvFilElementDtoer(minListeMedFilerSomErLagret))

		val uuid1SomIkkeErBlandtDokumentene = UUID.randomUUID().toString()

		listeAvUuiderSomSkalSlettes.add(uuid1SomIkkeErBlandtDokumentene)

		assertThrows<Exception> { slettFiler.slettFiler(listeAvUuiderSomSkalSlettes) }
	}
}
