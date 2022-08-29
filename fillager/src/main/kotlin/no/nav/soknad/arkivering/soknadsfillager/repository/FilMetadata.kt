package no.nav.soknad.arkivering.soknadsfillager.repository

import java.time.LocalDateTime

data class FilMetadata(
val id : String,
val status : String,
val created  : LocalDateTime? = null
)
