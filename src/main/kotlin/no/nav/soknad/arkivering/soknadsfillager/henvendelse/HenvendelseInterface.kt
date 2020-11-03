package no.nav.soknad.arkivering.soknadsfillager.henvendelse

import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto

interface HenvendelseInterface {

	fun fetchFile(uuid: String): FilElementDto?

	fun deleteFile(uuid: String): Boolean
}
