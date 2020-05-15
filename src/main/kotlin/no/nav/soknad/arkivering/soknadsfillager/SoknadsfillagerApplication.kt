package no.nav.soknad.arkivering.soknadsfillager

import no.nav.soknad.arkivering.soknadsfillager.db.RenewVaultService
import no.nav.soknad.arkivering.soknadsfillager.db.VaultCredentialService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SoknadsfillagerApplication

fun main(args: Array<String>) {
	runApplication<SoknadsfillagerApplication>(*args)
}
