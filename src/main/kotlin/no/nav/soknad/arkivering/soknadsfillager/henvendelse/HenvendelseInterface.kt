package no.nav.soknad.arkivering.soknadsfillager.henvendelse

interface HenvendelseInterface {

	fun fetchFile(uuid: String): ByteArray?

	fun deleteFile(uuid: String): Boolean

}
