package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilDbData
import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.supervision.FileMetrics
import no.nav.soknad.arkivering.soknadsfillager.supervision.Operations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DeleteFilesService(private val filRepository: FilRepository, private val fileMetrics: FileMetrics) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun deleteFiles(innsendingId: String?, ids: List<String>) {
		val countDelRecords = filRepository.deleteFiles(ids)
		if (ids.size != countDelRecords ) logger.warn(innsendingId + ": Number of deleted records does not match the number of requestied deletions " + ids)
	}

}
