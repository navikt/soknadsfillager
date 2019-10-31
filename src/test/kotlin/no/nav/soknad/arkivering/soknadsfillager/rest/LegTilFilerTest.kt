package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.MottateFilerDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class LegTilFilerTest {
	@Autowired
	private lateinit var leggTilFiler: LeggTilFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@Test
	fun enkelTestAvTjenste() {
		val uuid= UUID.randomUUID().toString()
		val blob = "Dette er min andre streng"  //TODO bytte ut med blob

		leggTilFiler.mottaDokumenter( MottateFilerDto(uuid, blob))

		assertTrue(mittRepository.findByUuid(uuid).isNotEmpty())

	}



}
