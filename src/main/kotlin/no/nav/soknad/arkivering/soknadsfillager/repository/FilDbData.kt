package no.nav.soknad.arkivering.soknadsfillager.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "filData")
class FilDbData (
	@Id
	val uuid: String,
	val melding: String
){

}
