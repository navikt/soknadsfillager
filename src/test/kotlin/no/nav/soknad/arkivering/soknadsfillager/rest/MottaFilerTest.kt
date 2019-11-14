package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.dto.MottaFilerDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class MottaFilerTest {
	@Autowired
	private lateinit var mottaFiler: MottaFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@Test
	fun enkelTestAvTjenste() {
		val uuid= UUID.randomUUID().toString()
		val blob = "Dette er min andre streng"  //TODO bytte ut med blob

		mottaFiler.mottaFiler( MottaFilerDto(uuid, blob))

		assertTrue(this.mittRepository.findByUuid(uuid).isNotEmpty())

	}



}
