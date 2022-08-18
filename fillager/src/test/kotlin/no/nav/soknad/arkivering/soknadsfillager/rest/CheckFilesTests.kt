package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.service.GetFilesService
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.head
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class CheckFilesTests {

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
	fun `Check files - happy case`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)

		mockMvc.head("/files/${filesToStore.ids()}").andExpect {
			status { isOk() }
		}
	}

	@Test
	fun `Check files - ids do not exist - throws NotFound`() {
		val nonExistentIds = "${UUID.randomUUID()},${UUID.randomUUID()}"

		mockMvc.head("/files/$nonExistentIds").andExpect {
			status { isNotFound() }
		}
	}

	@Test
	fun `Check files - all were deleted - throws Gone`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content =  "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "2".toByteArray(), createdAt = OffsetDateTime.now())
		)

		postFiles(filesToStore)
		deleteFiles(filesToStore.ids())

		mockMvc.head("/files/${filesToStore.ids()}").andExpect {
			status { isGone() }
		}
	}

	@Test
	fun `Check files - mixed status - throws Conflict`() {
		val filesToStore = listOf(
			FileData(UUID.randomUUID().toString(), content = "0".toByteArray(), createdAt = OffsetDateTime.now()),
			FileData(UUID.randomUUID().toString(), content = "1".toByteArray(), createdAt = OffsetDateTime.now())
		)
		val idThatDoesNotExist = UUID.randomUUID().toString()

		postFiles(filesToStore)
		deleteFiles(filesToStore[1].id)

		mockMvc.head("/files/${filesToStore.ids() + "," + idThatDoesNotExist}").andExpect {
			status { isConflict() }
		}
	}

	@Test
	fun `Check files - error retrieving`() {
		val crashingFilRepository = mockk<FilRepository>()
		every { crashingFilRepository.findById(any()) }.throws(JpaSystemException(RuntimeException("Mocked exception")))
		val crashingGetFilesService = GetFilesService(crashingFilRepository, fileMetrics)

		assertThrows<JpaSystemException> {
			crashingGetFilesService.getFiles(UUID.randomUUID().toString(), listOf(UUID.randomUUID().toString()))
		}
	}


	private fun deleteFiles(ids: String) = deleteFiles(mockMvc, ids)

	private fun postFiles(files: List<FileData>) = postFiles(mockMvc, mapper, files)


	private fun List<FileData>.ids() = this.joinToString(",") { it.id }
}
