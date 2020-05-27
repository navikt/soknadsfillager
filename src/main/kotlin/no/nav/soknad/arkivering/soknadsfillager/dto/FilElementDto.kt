package no.nav.soknad.arkivering.soknadsfillager.dto

import java.time.LocalDateTime

data class FilElementDto(val uuid: String, val fil: ByteArray?, val opprettet: LocalDateTime?) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as FilElementDto

		if (uuid != other.uuid) return false

		return true
	}

	override fun hashCode(): Int {
		var result = uuid.hashCode()
		if (fil != null) {
			result = 31 * result + fil.contentHashCode()
		}
		return result
	}
}
