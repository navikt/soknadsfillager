package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

internal class HentFilerTest {
	@Autowired
	private lateinit var hentFiler: HentFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun hentDokumenter() {
			val uuid = UUID.randomUUID().toString()

    }
}
