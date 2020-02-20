package no.nav.soknad.arkivering.soknadsfillager.repository

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "documents")
data class FilDbData(@Id @Column(name = "id") val uuid: String, @Column(name = "data", columnDefinition = "bytea") val data: ByteArray?) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as FilDbData

		if (uuid != other.uuid) return false
		if (data != null) {
			if (other.data == null) return false
			if (!data.contentEquals(other.data)) return false
		} else if (other.data != null) return false

		return true
	}

	override fun hashCode(): Int {
		var result = uuid.hashCode()
		result = 31 * result + (data?.contentHashCode() ?: 0)
		return result
	}
}
