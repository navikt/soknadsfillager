package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilMetadata
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class GetFilesService(private val filRepository: FilRepository) {

	fun getFilesMetadata(innsendingId: String?, ids: List<String>): List<FileData> {
		val filMetadata = filRepository.findFilesMetadata(ids)
		val filData = ids.map { id -> filMetadata.firstOrNull { it.id == id } ?: FilMetadata(id, status = statusNotFound) }
		return filData.map { FileData(id = it.id, status = it.status) }
	}

	fun getFiles(innsendingId: String?, ids: List<String>): List<FileData> {

		val filer = filRepository.findAllById(ids)

		return ids.map { id ->
			val fil = filer.firstOrNull { it.uuid == id }
			if (fil == null) FileData(id = id, status = statusNotFound)
			else FileData(
				id = fil.uuid,
				content = fil.document,
				createdAt = fil.created?.atOffset(ZoneOffset.UTC),
				status = if (fil.document != null) statusOk else statusDeleted
			)
		}
	}
}

const val statusOk = "ok"
const val statusDeleted = "deleted"
const val statusNotFound = "not-found"
