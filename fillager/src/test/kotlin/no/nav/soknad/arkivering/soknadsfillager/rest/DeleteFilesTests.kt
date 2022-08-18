package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.DeleteFilesService
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
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class DeleteFilesTests {

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
	fun `Deleting files - happy case`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.DELETE.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.DELETE.name)
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content ="1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content ="2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertEquals(3, filRepository.count())

		deleteFiles(filesToStore.ids())

		assertEquals(3, filRepository.count())
		mockMvc.get("/files/${filesToStore.ids()}").andExpect {
			status { isGone() }
		}
		assertEquals(fileCounter!! + filesToStore.size + 0.0, fileMetrics.filCounterGet(Operations.DELETE.name))
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.DELETE.name))
		assertTrue(fileMetrics.filSummaryLatencyGet(Operations.DELETE.name).sum > 0)
		assertEquals(fileCounter + filesToStore.size + 1.0, fileMetrics.filSummaryLatencyGet(Operations.DELETE.name).count)
	}

	@Test
	fun `Deleting files - delete one file`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)
		val idToDelete = filesToStore[1].id

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertEquals(3, filRepository.count())

		deleteFiles(idToDelete)

		assertEquals(3, filRepository.count())
		mockMvc.get("/files/$idToDelete").andExpect {
			status { isGone() }
		}
		mockMvc.get("/files/${listOf(filesToStore[0], filesToStore[2]).ids()}").andExpect {
			status { isOk() }
		}
	}

	@Test
	fun `Deleting files - delete same file twice`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)
		val idToDelete = filesToStore[1].id

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertEquals(3, filRepository.count())

		deleteFiles("$idToDelete,$idToDelete")

		assertEquals(3, filRepository.count())
		mockMvc.get("/files/$idToDelete").andExpect {
			status { isGone() }
		}
		mockMvc.get("/files/${listOf(filesToStore[0], filesToStore[2]).ids()}").andExpect {
			status { isOk() }
		}
	}

	@Test
	fun `Deleting files - return even if one ids does not exist`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)
		val nonExistentId = UUID.randomUUID().toString()

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertEquals(3, filRepository.count())

		deleteFiles(filesToStore.ids() + ",$nonExistentId")

		assertEquals(3, filRepository.count())
		mockMvc.get("/files/${filesToStore.ids()}").andExpect {
			status { isGone() }
		}
		mockMvc.get("/files/$nonExistentId").andExpect {
			status { isNotFound() }
		}
	}

	@Test
	fun `Deleting files - error saving`() {
		val crashingFilRepository = mockk<FilRepository>()
		every { crashingFilRepository.save(any()) }.throws(JpaSystemException(RuntimeException("Mocked exception")))
		every { crashingFilRepository.findById(any()) }
			.returns(Optional.of(FilDbData(UUID.randomUUID().toString(), "0".toByteArray(), LocalDateTime.now())))
		val crashingDeleteFilesService = DeleteFilesService(crashingFilRepository, fileMetrics)

		assertThrows<JpaSystemException> {
			crashingDeleteFilesService.deleteFiles(UUID.randomUUID().toString(), listOf(UUID.randomUUID().toString()))
		}
	}


	private fun getFiles(ids: String) = getFiles(mockMvc, mapper, ids)

	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
