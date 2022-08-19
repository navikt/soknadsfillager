package no.nav.soknad.arkivering.soknadsfillager.rest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

fun getFiles(mockMvc: MockMvc, mapper: ObjectMapper, ids: String, metadataOnly:Boolean = false): List<FileData> {
	val metadataOnlyString = if (metadataOnly) "?metadataOnly=true" else ""
	val result = mockMvc.get("/files/$ids$metadataOnlyString") {
		accept = MediaType.APPLICATION_JSON
	}.andExpect {
		status { isOk() }
		content { contentType(MediaType.APPLICATION_JSON) }
	}.andReturn()

	return mapper.readValue(result.response.contentAsString, object : TypeReference<List<FileData>>() {})
}

fun postFiles(mockMvc: MockMvc, mapper: ObjectMapper, input: List<FileData>) {
	mockMvc.post("/files") {
		contentType = MediaType.APPLICATION_JSON
		content = mapper.writeValueAsString(input)
	}.andExpect {
		status { isOk() }
	}
}

fun deleteFiles(mockMvc: MockMvc, ids: String) {
	mockMvc.delete("/files/$ids")
		.andExpect {
			status { isOk() }
		}
}

fun assertFilesEqual(expectedFiles: List<FileData>, actualFiles: List<FileData>) {
	assertEquals(expectedFiles.size, actualFiles.size)

	expectedFiles.forEach { expected ->
		val actual = actualFiles.first { it.id == expected.id }
		assertArrayEquals(expected.content, actual.content)
		assertEquals(expected.createdAt?.toEpochSecond(), actual.createdAt?.toEpochSecond())
	}
}
