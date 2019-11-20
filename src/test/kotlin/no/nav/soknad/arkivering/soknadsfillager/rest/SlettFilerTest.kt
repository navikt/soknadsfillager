package no.nav.soknad.arkivering.soknadsfillager.rest

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.util.*


@SpringBootTest
class SlettFilerTest {
	@Autowired
	private lateinit var slettFiler: SlettFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@Autowired
	private lateinit var mottaFiler: MottaFiler

	@AfterEach
	fun ryddOpp(){
		mittRepository.deleteAll()
	}

	@Test
	fun slettFiler(){

		val (uuid1, uuid2, uuid3) = OpprettRepoMedTreFiler()
		val listeAvMineUuiderSomSkalSlettes = listOf<String>(uuid1, uuid2, uuid3)

		assertEquals(3, mittRepository.count().toInt())

		slettFiler.slettFiler(listeAvMineUuiderSomSkalSlettes)

		assertEquals(0, mittRepository.count().toInt())

	}

	@Disabled
	@Test
	fun slettFilSomIkkeFinnes(){
		val listeMedUuidSomIkkeFinnes = listOf<String>(UUID.randomUUID().toString())
		val (uuid1, uuid2, uuid3) = OpprettRepoMedTreFiler()

		assertThrows<Exception> { slettFiler.slettFiler(listeMedUuidSomIkkeFinnes) }

	}


	private fun OpprettRepoMedTreFiler(): Triple<String, String, String> {
		val uuid1 = UUID.randomUUID().toString()
		val uuid2 = UUID.randomUUID().toString()
		val uuid3 = UUID.randomUUID().toString()
		val fil1 = "fil$uuid1"
		val fil2 = "fil$uuid2"
		val fil3 = "fil$uuid3"

		val mottattFil1 = FilElementDto(uuid1, fil1)
		val mottattFil2 = FilElementDto(uuid2, fil2)
		val mottaFiler3 = FilElementDto(uuid3, fil3)

		val minListeAvMottatteFiler = listOf<FilElementDto>(mottattFil1, mottattFil2, mottaFiler3)

		mottaFiler.mottaFiler(minListeAvMottatteFiler)

		return Triple(uuid1, uuid2, uuid3)
	}
}
