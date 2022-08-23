package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.statusDeleted
import no.nav.soknad.arkivering.soknadsfillager.service.statusNotFound
import no.nav.soknad.arkivering.soknadsfillager.service.statusOk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class CheckFilesTests {

	@Autowired
	private lateinit var filRepository: FilRepository

	@Autowired
	private lateinit var mapper: ObjectMapper

	@Autowired
	private lateinit var mockMvc: MockMvc

	@AfterEach
	fun emptyDatabase() = filRepository.deleteAll()


	@Test
	fun `Check files - with ok status`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)

		val persistedFiles = getFileMetadata(filesToStore.ids())
		assertTrue(persistedFiles.all { it.status == statusOk })
		assertTrue(persistedFiles.all { it.content == null })
	}

	@Test
	fun `Check files - with status not-found`() {

		val nonExistentIds = "${UUID.randomUUID()},${UUID.randomUUID()}"
		val nonExistentFiles = getFileMetadata(nonExistentIds)

		assertEquals(nonExistentIds, nonExistentFiles.ids())
		assertTrue(nonExistentFiles.all { it.status == statusNotFound })
	}

	@Test
	fun `Check files - with deleted status`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)
		deleteFiles(filesToStore.ids())

		val persistedFiles = getFileMetadata(filesToStore.ids())
		assertTrue(persistedFiles.all { it.content == null })
		assertTrue(persistedFiles.all { it.status == statusDeleted })
	}


	private fun getFileMetadata(ids: String) = getFiles(mockMvc, mapper, ids, true)

	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
