package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class GetFilesTests {

	@Autowired
	private lateinit var filRepository: FilRepository
	@Autowired
	private lateinit var fileMetrics: FileMetrics
	@Autowired
	private lateinit var mapper: ObjectMapper
	@Autowired
	private lateinit var mockMvc: MockMvc

	@AfterEach
	fun emptyDatabase() = filRepository.deleteAll()


	@Test
	fun `Get files - happy case`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.FIND.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.FIND.name)
		val fileNotFoundCounter = fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name)
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), "0".toByteArray(), OffsetDateTime.now().minusHours(1)),
			FileData(UUID.randomUUID().toString(), "1".toByteArray(), OffsetDateTime.now().minusMinutes(2)),
			FileData(UUID.randomUUID().toString(), "2".toByteArray(), OffsetDateTime.now().minusSeconds(3))
		)

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertEquals(fileCounter!! + 3.0, fileMetrics.filCounterGet(Operations.FIND.name)!!)
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.FIND.name)!!)
		assertEquals(fileNotFoundCounter!! + 0.0, fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name))
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0)
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).count >= fileCounter + 3.0)
	}

	@Test
	fun `Get files - ids do not exist - throws NotFound`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.FIND.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.FIND.name)
		val fileNotFoundCounter = fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name)

		val nonExistentIds = "${UUID.randomUUID()},${UUID.randomUUID()}"

		mockMvc.get("/files/$nonExistentIds").andExpect {
			status { isNotFound() }
		}
		assertEquals(fileCounter!! + 0.0, fileMetrics.filCounterGet(Operations.FIND.name)!!)
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.FIND.name)!!)
		assertEquals(fileNotFoundCounter!! + 2.0, fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name))
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0)
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).count >= fileCounter + 1.0)
	}

	@Test
	fun `Get files - all were deleted - throws Gone`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.FIND.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.FIND.name)
		val fileNotFoundCounter = fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name)

		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), "0".toByteArray(), OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), "1".toByteArray(), OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), "2".toByteArray(), OffsetDateTime.now())
		)

		postFiles(filesToStore)
		deleteFiles(filesToStore.ids())

		mockMvc.get("/files/${filesToStore.ids()}").andExpect {
			status { isGone() }
		}
		assertEquals(fileCounter!! + 3.0, fileMetrics.filCounterGet(Operations.FIND.name)!!)
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.FIND.name)!!)
		assertEquals(fileNotFoundCounter!! + 0.0, fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name))
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0)
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).count >= fileCounter + 1.0)
	}

	@Test
	fun `Get files - mixed status - throws Conflict`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.FIND.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.FIND.name)
		val fileNotFoundCounter = fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name)

		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), "0".toByteArray(), OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), "1".toByteArray(), OffsetDateTime.now())
		)
		val idThatDoesNotExist = UUID.randomUUID().toString()

		postFiles(filesToStore)
		deleteFiles(filesToStore[1].id)

		mockMvc.get("/files/${filesToStore.ids() + "," + idThatDoesNotExist}").andExpect {
			status { isConflict() }
		}

		assertEquals(fileCounter!! + 2.0, fileMetrics.filCounterGet(Operations.FIND.name)!!)
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.FIND.name)!!)
		assertEquals(fileNotFoundCounter!! + 1.0, fileMetrics.filCounterGet(Operations.FIND_NOT_FOUND.name))
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).sum > 0)
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.FIND.name).count >= fileCounter + 1.0)
	}

	@Test
	fun `Get files - error retrieving`() {
		val crashingFilRepository = mockk<FilRepository>()
		every { crashingFilRepository.findById(any()) }.throws(JpaSystemException(RuntimeException("Mocked exception")))
		val crashingGetFilesService = GetFilesService(crashingFilRepository, fileMetrics)

		assertThrows<JpaSystemException> {
			crashingGetFilesService.getFiles(UUID.randomUUID().toString(), listOf(UUID.randomUUID().toString()))
		}
	}


	private fun getFiles(ids: String) = getFiles(mockMvc, mapper, ids)

	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
