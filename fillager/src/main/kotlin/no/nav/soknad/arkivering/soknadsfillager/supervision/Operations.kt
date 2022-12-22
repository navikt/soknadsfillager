package no.nav.soknad.arkivering.soknadsfillager.supervision

enum class Operations(name: String) {
	FIND("find"),
	FIND_NOT_FOUND("find_not_found"),
	SAVE("save"),
	DELETE("delete")
}
