package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilMetadata
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.ConflictException
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileGoneException
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileNotSeenException
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class GetFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun getFilesMetadata(innsendingId: String?, ids: List<String>) : List<FileData>{
		val filMetadata = filRepository.findFilesMetadata(ids)
		val filData =  ids.map { id -> filMetadata.firstOrNull { it.id == id } ?: FilMetadata(id, status ="not-found") }
		return filData.map { FileData(id = it.id,status = it.status) }
	}

	fun getFiles(innsendingId: String?, ids: List<String>): List<FileData> {

		val filer : Iterable<FilDbData> = filRepository.findAllById(ids)

		val filData  = ids.map { id ->  val fil = filer.firstOrNull{ it.uuid == id }
																		if (fil == null) FileData(id = id,status = "not-found")
																		else FileData(id = fil.uuid,
																									content = fil.document ,
																									createdAt = fil.created?.atOffset(ZoneOffset.UTC),
																									status = if (fil.document != null) "ok" else "deleted")
						}
		return filData
	}

}
