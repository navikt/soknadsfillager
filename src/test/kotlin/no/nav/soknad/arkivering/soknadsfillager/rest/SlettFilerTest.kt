package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
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
    fun ryddOpp() {
        mittRepository.deleteAll()
    }

    @Test
    fun slettFiler() {

        val listeAvMineDokumenterSomSkalSlettes =
                lagreEnListeAvDokumenter()

        assertEquals(3, mittRepository.count().toInt())

        val listeAvMineUuiderSomSkalSlettes = listeAvMineDokumenterSomSkalSlettes.map { it.uuid }

        slettFiler.slettFiler(listeAvMineUuiderSomSkalSlettes)

        assertEquals(0, mittRepository.count().toInt())
    }

    @Test
    fun slettFilSomIkkeFinnes() {
        val minListeMedFilerSomErLagret = lagreEnListeAvDokumenter() // A, B, C
        val slettelisteMedEkstraUuid = minListeMedFilerSomErLagret.map { it.uuid }.toMutableList()

        slettelisteMedEkstraUuid.add(1, UUID.randomUUID().toString()) // A, B, C og D som ikke er i basen

        slettFiler.slettFiler(slettelisteMedEkstraUuid)

        assertEquals(0, mittRepository.count().toInt())
    }


    @Test
    fun slettFilersomLiggerFlerGangerIListen(){

        val list = lagreEnListeAvDokumenter() // A, B, C
        val sletteListe = list.plus(list.first()) // A, B, C, A

        slettFiler.slettFiler(sletteListe.map { it.uuid })

        assertEquals(0, mittRepository.count().toInt())
    }

    private fun lagreEnListeAvDokumenter(): List<FilElementDto> {
        return opprettListeAv3FilDtoer().also { lagreMineFiler(it) }
    }

    private fun lagreMineFiler(list: List<FilElementDto>) = this.lagreFiler.lagreFiler(list)

}
