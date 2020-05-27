package no.nav.soknad.arkivering.soknadsfillager.repository

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "documents")
data class FilDbData(@Id @Column(name = "id") val uuid: String, @Column(name = "document", columnDefinition = "bytea") val document: ByteArray?, @Column(name = "created", columnDefinition = "TIMESTAMP WITH TIME ZONE") val created: LocalDateTime?) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as FilDbData

		if (uuid != other.uuid) return false

		return true
	}

	override fun hashCode(): Int {
		var result = uuid.hashCode()
		result = 31 * result + (document?.contentHashCode() ?: 0)
		return result
	}
}
