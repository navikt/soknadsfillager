package no.nav.soknad.arkivering.soknadsfillager

data class ApplicationState(
	var alive: Boolean = true,
	var ready: Boolean = false
)

