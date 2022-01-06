package no.nav.soknad.arkivering.soknadsfillager.rest

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileNotSeenException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class SjekkFilerTest {

	private val filesInDb = opprettListeAv3FilDtoer()

	@Autowired
	private lateinit var sjekkFiler: SjekkFiler
	@Autowired
	private lateinit var lagreFiler: LagreFiler

	@Autowired
	private lateinit var filRepository: FilRepository

	@BeforeEach
	private fun setup() {
		lagreFiler.lagreFiler(filesInDb)
	}

	@AfterEach
	fun teardown() {
		filRepository.deleteAll()
	}


	@Test
	fun `Checks empty list of ids - returns fine`() {
		val fileIds = emptyList<String>()

		sjekkFiler.sjekkFiler(fileIds) // Does not throw exception
	}

	@Test
	fun `Finds all files - returns fine`() {
		val fileIds = filesInDb.map { it.uuid }

		sjekkFiler.sjekkFiler(fileIds) // Does not throw exception
	}

	@Test
	fun `Finds no files - returns Exception`() {
		val fileIds = listOf(UUID.randomUUID().toString(), UUID.randomUUID().toString())

		val exception = assertThrows<FileNotSeenException> {
			sjekkFiler.sjekkFiler(fileIds)
		}
		assertEquals(fileIds.toString(), exception.message)
	}

	@Test
	fun `Finds some files - returns Exception`() {
		val unseenId = UUID.randomUUID().toString()
		val fileIds = filesInDb.map { it.uuid }.plus(unseenId)

		val exception = assertThrows<FileNotSeenException> {
			sjekkFiler.sjekkFiler(fileIds)
		}
		assertEquals(listOf(unseenId).toString(), exception.message)
	}
}
