package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.soknad.arkivering.soknadsfillager.ApplicationTest
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.StoreFilesService
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.web.servlet.MockMvc
import java.time.OffsetDateTime
import java.util.*

class PostFilesTests : ApplicationTest() {

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
	fun `Posting files - happy case`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.SAVE.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.SAVE.name)
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
		assertEquals(fileCounter!! + filesToStore.size.toDouble(), fileMetrics.filCounterGet(Operations.SAVE.name))
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.SAVE.name))
	}

	@Test
	fun `Posting files - replaces file`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.SAVE.name)
		val errorCounter = fileMetrics.errorCounterGet(Operations.SAVE.name)
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
		val newFile = FileData(filesToStore[1].id, content = "new".toByteArray(), createdAt = OffsetDateTime.now())

		postFiles(filesToStore)
		assertFilesEqual(filesToStore, getFiles(filesToStore.ids()))

		postFiles(listOf(newFile)) // Replace file
		assertFilesEqual(listOf(filesToStore[0], newFile, filesToStore[2]), getFiles(filesToStore.ids()))

		assertEquals(fileCounter!! + 1.0 + filesToStore.size, fileMetrics.filCounterGet(Operations.SAVE.name))
		assertEquals(errorCounter!! + 0.0, fileMetrics.errorCounterGet(Operations.SAVE.name))
	}

	@Test
	fun `Posting files - error saving`() {
		val crashingFilRepository = mockk<FilRepository>()
		every { crashingFilRepository.save(any()) }.throws(JpaSystemException(RuntimeException("Mocked exception")))
		val crashingStoreFilesService = StoreFilesService(crashingFilRepository, fileMetrics)
		val files =
			listOf(FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()))

		assertThrows<JpaSystemException> {
			crashingStoreFilesService.storeFiles(files)
		}
	}

	@Test
	fun `Posting file entry - empty file`() {
		val fileCounter = fileMetrics.filCounterGet(Operations.SAVE.name)
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
			FileData(UUID.randomUUID().toString(), content = null, createdAt = OffsetDateTime.now().minusSeconds(3))
		)

		postWithEmptyFiles(filesToStore)
		val filesStored = filesToStore.filter { it.content != null }

		assertFilesEqual(filesStored, getFiles(filesStored.ids()))
		assertEquals(fileCounter!! + filesStored.size.toDouble(), fileMetrics.filCounterGet(Operations.SAVE.name))
	}


	private fun getFiles(ids: String) = getFiles(mockMvc, mapper, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)

	private fun postWithEmptyFiles(files: List<FileData>) = postWithEmptyFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
