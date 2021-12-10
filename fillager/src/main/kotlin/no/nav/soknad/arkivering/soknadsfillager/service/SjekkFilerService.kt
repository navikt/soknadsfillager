package no.nav.soknad.arkivering.soknadsfillager.service

import no.nav.soknad.arkivering.soknadsfillager.repository.FilRepository
import no.nav.soknad.arkivering.soknadsfillager.rest.exception.FileNotSeenException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SjekkFilerService(private val filRepository: FilRepository) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun sjekkFiler(fileIds: List<String>) {
		val nonExistentFiles = fileIds.filter { !filRepository.existsById(it) }

		if (nonExistentFiles.isNotEmpty()) {
			logger.info("These files do not exist: $nonExistentFiles")
			throw FileNotSeenException(nonExistentFiles.toString())
		}
	}
}
