package no.nav.soknad.arkivering.soknadsfillager.supervision

enum class Operations(name: String) {
	FIND("find"),
	FIND_HENVENDELSE("find_henvendelse"),
	FIND_NOT_FOUND("find_not_found"),
	SAVE("save"),
	DELETE("saveAndFlush_clear")
}
