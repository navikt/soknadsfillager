package no.nav.soknad.arkivering.soknadsfillager.db

enum class Role {
	ADMIN, USER;

	override fun toString() = name.lowercase()
}
