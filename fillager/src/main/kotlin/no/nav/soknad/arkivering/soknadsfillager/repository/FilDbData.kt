package no.nav.soknad.arkivering.soknadsfillager.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
class FilDbData(
	@Id @Column(name = "id") val uuid: String,
	@Column(name = "document", columnDefinition = "bytea")
	val document: ByteArray?,
	@Column(name = "created", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	val created: LocalDateTime?,
	@Column(name = "status")
	val status: String,
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as FilDbData

		return uuid == other.uuid
	}

	override fun hashCode() = uuid.hashCode()
}
