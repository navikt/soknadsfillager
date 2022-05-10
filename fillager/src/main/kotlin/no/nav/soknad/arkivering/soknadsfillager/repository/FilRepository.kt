package no.nav.soknad.arkivering.soknadsfillager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FilRepository : JpaRepository<FilDbData, String> {

	@Query(value = "SELECT count(id) FROM documents where document is not null", nativeQuery = true)
	fun documentCount(): Long
}
