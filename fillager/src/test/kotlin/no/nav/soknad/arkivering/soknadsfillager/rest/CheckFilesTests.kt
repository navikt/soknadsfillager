package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.head
import java.io.File
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

		val persistedFiles = getFiles(filesToStore.ids())
		assertTrue(persistedFiles.all { it.status == "ok" })
		assertTrue(persistedFiles.all { it.content == null })

		mockMvc.get("/files/${filesToStore.ids()}?metadataOnly=true").andExpect {
			status { isOk() }
		}
	}

	@Test
	fun `Check files - with status not-found`() {

		val nonExistentIds = "${UUID.randomUUID()},${UUID.randomUUID()}"
		val nonExistentFiles = getFiles(nonExistentIds)

		Assertions.assertEquals(nonExistentIds, nonExistentFiles.ids())
		assertTrue( nonExistentFiles.all { it.status == "not-found" } )


	}

	@Test
	fun `Check files - with deleted status`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content =  "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)
		deleteFiles(filesToStore.ids())

		val persistedFiles = getFiles(filesToStore.ids())
		assertTrue( persistedFiles.all { it.content == null } )
		assertTrue(persistedFiles.all { it.status == "deleted"})
	}




	private fun getFiles(ids: String) = getFiles(mockMvc, mapper, ids,true)

	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
