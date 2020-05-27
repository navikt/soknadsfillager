package no.nav.soknad.arkivering.soknadsfillager.db

enum class Role {
	ADMIN, USER, READONLY;

	override fun toString() = name.toLowerCase()

}
