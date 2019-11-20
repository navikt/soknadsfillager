package no.nav.soknad.arkivering.soknadsfillager.rest

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import no.nav.soknad.arkivering.soknadsfillager.dto.MottaFilerDto
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

internal class HentFilerTest {
	@Autowired
	private lateinit var mottaFiler: MottaFiler

	@Autowired
	private lateinit var hentFiler: HentFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	val uuid = UUID.randomUUID().toString()

    @BeforeEach
    fun setUp() {
			val blob ="Dette er min andre streng"

			//mottaFiler.mottaFiler(MottaFilerDto(uuid, blob))

    }

 /*   @Test
    fun hentDokumenterTest() {
			 val other = uuid
			hentFiler.hentDokumenter(this.uuid)

			assertTrue(this.uuid == other)
    }*/
}
