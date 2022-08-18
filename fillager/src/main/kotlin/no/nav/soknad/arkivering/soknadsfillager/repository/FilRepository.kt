package no.nav.soknad.arkivering.soknadsfillager.repository

import no.nav.soknad.arkivering.soknadsfillager.model.FileData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FilRepository : CrudRepository<FilDbData, String> {

	@Query(value = "SELECT count(id) FROM documents where document is not null", nativeQuery = true)
	fun documentCount(): Long

  @Query("select new no.nav.soknad.arkivering.soknadsfillager.repository.FilMetadata( p.uuid, case when(p.document is not null ) then \"ok\" else \"deleted\" end , p.created)  from FilDbData p")
	fun findFilesMetadata(ids : List<String>) : List<FilMetadata>

}

