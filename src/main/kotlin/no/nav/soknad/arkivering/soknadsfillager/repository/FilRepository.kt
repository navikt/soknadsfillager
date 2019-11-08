package no.nav.soknad.arkivering.soknadsfillager.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FilRepository: MongoRepository<FilDbData, String> {
	fun findByUuid(uuid: FilDbData): List<FilDbData>
}
