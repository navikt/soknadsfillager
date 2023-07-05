package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.soknad.arkivering.soknadsfillager.ApplicationTest
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.service.statusDeleted
import no.nav.soknad.arkivering.soknadsfillager.service.statusNotFound
import no.nav.soknad.arkivering.soknadsfillager.service.statusOk
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.OffsetDateTime
import java.util.*

class GetFilesTests : ApplicationTest() {

	@Autowired
	private lateinit var filRepository: FilRepository

	@Autowired
	private lateinit var mapper: ObjectMapper

	@Autowired
	private lateinit var mockMvc: MockMvc

	@AfterEach
	fun emptyDatabase() = filRepository.deleteAll()


	@Test
	fun `Get files - happy case`() {

		val filesToStore = listOf(
			FileData(
				UUID.randomUUID().toString(),
				content = "0".toByteArray(),
				createdAt = OffsetDateTime.now().minusHours(1)
			),
			FileData(
				UUID.randomUUID().toString(),
				content = "1".toByteArray(),
				createdAt = OffsetDateTime.now().minusMinutes(2)
			),
			FileData(
				UUID.randomUUID().toString(),
				content = "2".toByteArray(),
				createdAt = OffsetDateTime.now().minusSeconds(3)
			)
		)

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))
		assertTrue(getFiles(filesToStore.ids()).all { it.status == statusOk })
	}

	@Test
	fun `Get files - ids do not exist - all statuses are not-found`() {

		val nonExistentIds = "${UUID.randomUUID()},${UUID.randomUUID()}"
		val nonExistentFiles = getFiles(nonExistentIds)

		assertEquals(nonExistentIds, nonExistentFiles.ids())
		assertTrue(nonExistentFiles.all { it.status == statusNotFound })
	}

	@Test
	fun `Get files - one id do not exist`() {

		val filesToStore = listOf(
			FileData(
				UUID.randomUUID().toString(),
				content = "0".toByteArray(),
				createdAt = OffsetDateTime.now().minusHours(1)
			),
			FileData(
				UUID.randomUUID().toString(),
				content = "1".toByteArray(),
				createdAt = OffsetDateTime.now().minusMinutes(2)
			),
			FileData(
				UUID.randomUUID().toString(),
				content = "2".toByteArray(),
				createdAt = OffsetDateTime.now().minusSeconds(3)
			)
		)

		postFiles(filesToStore)
		val existingAndNotExistingIds = filesToStore.ids() + ", ${UUID.randomUUID()}"
		val fileList = getFiles(existingAndNotExistingIds)

		assertTrue(fileList.any { it.status == statusNotFound } && fileList.any { it.status == statusOk })

		val metaFileList = getFiles(existingAndNotExistingIds, true)
		assertTrue(fileList.all { equalState(it, metaFileList) })
	}

	private fun equalState(withFiles: FileData, metaFileList: List<FileData>): Boolean {
		if (metaFileList == null || metaFileList.isEmpty()) return false
		val metaFile = metaFileList.find { it.id == withFiles.id }
		return if (metaFile == null) false else withFiles.status == metaFile.status
	}

	@Test
	fun `Get files - all were deleted - all statuses are deleted `() {

		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)
		deleteFiles(filesToStore.ids())
		val allFilesDeleted = getFiles(filesToStore.ids()).all { it.status == statusDeleted }
		assertTrue(allFilesDeleted)

		mockMvc.get("/files/${filesToStore.ids()}").andExpect {
			status { isOk() }
		}
	}

	@Test
	fun `Get files - error retrieving`() {
		val crashingFilRepository = mockk<FilRepository>()
		val fileMetrics = mockk<FileMetrics>(relaxed = true)
		every { crashingFilRepository.findAllById(any()) }.throws(JpaSystemException(RuntimeException("Mocked exception")))
		val crashingGetFilesService = GetFilesService(crashingFilRepository, fileMetrics)

		assertThrows<JpaSystemException> {
			crashingGetFilesService.getFiles(UUID.randomUUID().toString(), listOf(UUID.randomUUID().toString()))
		}
	}


	private fun getFiles(ids: String) = getFiles(mockMvc, mapper, ids)
	private fun getFiles(ids: String, metadataOnly: Boolean) = getFiles(mockMvc, mapper, ids, metadataOnly)

	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
