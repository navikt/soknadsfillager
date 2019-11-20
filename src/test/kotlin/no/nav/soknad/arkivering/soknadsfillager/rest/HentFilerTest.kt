package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HentFilerTest {
	@Autowired
	private lateinit var mottaFiler: MottaFiler

	@Autowired
	private lateinit var hentFiler: HentFiler

	@Autowired
	private lateinit var mittRepository: FilRepository

	@AfterEach
	fun ryddOpp(){
		mittRepository.deleteAll()
	}

	@Test
    fun hentEtDokumentTest() {
		val (minUuid, minFil) = opprettEnEnkelFil()

		val minMotattFilIListe =
			opprettMottattFilListeMedBareEnFil(minUuid, minFil)

		this.mottaFiler.mottaFiler(minMotattFilIListe)

			val mittDokumentSomSkalHentes =
				listOf<String>(minUuid)

				hentFiler.hentDokumenter(mittDokumentSomSkalHentes)

		assertTrue(this.mittRepository.findByUuid(minUuid).isNotEmpty())
    }

	@Test
	fun  hentEnListeAvDokumenterTest(){
		val mineFilerListe = opprett3Filer()

		this.mottaFiler.mottaFiler(mineFilerListe)

		val minUuidListe=  hentUtenListeAvUuiderFraListeAVFilElementDtoer(mineFilerListe)

		assertTrue(this.mittRepository.findByUuid(minUuidListe.first()).isNotEmpty())
		assertTrue(this.mittRepository.findById(minUuidListe.last()).isPresent)

	}
}
