package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.statusDeleted
import no.nav.soknad.arkivering.soknadsfillager.service.statusNotFound
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class DeleteFilesTests {

	@Autowired
	private lateinit var filRepository: FilRepository

	@Autowired
	private lateinit var mapper: ObjectMapper

	@Autowired
	private lateinit var mockMvc: MockMvc

	@AfterEach
	fun emptyDatabase() = filRepository.deleteAll()


	@Test
	fun `Deleting files - ensure content is deleted`() {

		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertEquals(3, filRepository.count())

		deleteFiles(filesToStore.ids())

		val deletedFiles = getFiles(filesToStore.ids())
		assertEquals(3, deletedFiles.size)
		assertTrue(deletedFiles.all { it.content == null })
		assertTrue(deletedFiles.all { it.status == statusDeleted })
	}

	@Test
	fun `Deleting files - delete same file twice`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)

		val persistedFiles = getFiles(filesToStore.ids())
		assertFilesEqual(filesToStore, persistedFiles)

		deleteFiles(filesToStore.ids())
		deleteFiles(filesToStore.ids())

		val deletedFiles = getFiles(filesToStore.ids())
		assertEquals(3, deletedFiles.size)
		assertTrue(deletedFiles.all { it.content == null })
		assertTrue(deletedFiles.all { it.status == statusDeleted })
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

		deleteFiles(filesToStore.ids() + ",$nonExistentId")

		val persistedFiles = getFiles(filesToStore.ids() + ",$nonExistentId")
		assertEquals(4, persistedFiles.size)
		assertDoesNotThrow { persistedFiles.first { it.id == nonExistentId } }
		assertTrue(persistedFiles.first { it.id == nonExistentId }.status == statusNotFound)
	}

	private fun getFiles(ids: String) = getFiles(mockMvc, mapper, ids)

	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)

	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
