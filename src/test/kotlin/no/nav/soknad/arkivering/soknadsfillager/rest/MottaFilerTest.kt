package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.util.*


@SpringBootTest
class MottaFilerTest {
	@Autowired
	private lateinit var mottaFiler: MottaFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@AfterEach
	fun ryddOpp(){
		mittRepository.deleteAll()
	}

	@Test
	fun enkelTestAvTjenste() {
		val minUuid= UUID.randomUUID().toString()
		val minFil = "Dette er min andre streng"  //TODO bytte ut med blob
		//val fil = "src/test/resources/navlogo.pdf"
		val mittFilElement = FilElementDto(minUuid, minFil)
		val minliste = listOf<FilElementDto>(mittFilElement)

		mottaFiler.mottaFiler(minliste)

		assertTrue(this.mittRepository.findByUuid(minUuid).isNotEmpty())

	}

}
