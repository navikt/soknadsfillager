package no.nav.soknad.arkivering.soknadsfillager.repository

import no.nav.soknad.arkivering.soknadsfillager.service.statusDeleted
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface FilRepository : CrudRepository<FilDbData, String> {

	@Query(value = "SELECT count(id) FROM documents where document is not null", nativeQuery = true)
	fun documentCount(): Long

	@Query("select new no.nav.soknad.arkivering.soknadsfillager.repository.FilMetadata(p.uuid, p.status, p.created) from FilDbData p where p.uuid in :ids")
	fun findFilesMetadata(ids: List<String>): List<FilMetadata>

	@Modifying
	@Query("update FilDbData f set f.document=null, f.status='$statusDeleted' where f.uuid in :ids")
	@Transactional
	fun deleteFiles(ids: List<String>): Int
}
