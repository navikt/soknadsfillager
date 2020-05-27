package no.nav.soknad.arkivering.soknadsfillager.db

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import org.slf4j.LoggerFactory

class RenewVaultService(private val vaultCredentialService: VaultCredentialService, private val applicationState: ApplicationState) {

	private val log = LoggerFactory.getLogger("no.nav.soknad.arkivering.soknadsfillager.RenewVaultService")

	fun startRenewTasks() {
		GlobalScope.launch {
			try {
				Vault.renewVaultTokenTask(applicationState)
			} catch (e: Exception) {
				log.error("Noe gikk galt ved fornying av vault-token", e.message)
			} finally {
				applicationState.ready = false
			}
		}

		GlobalScope.launch {
			try {
				vaultCredentialService.runRenewCredentialsTask(applicationState)
			} catch (e: Exception) {
				log.error("Noe gikk galt ved fornying av vault-credentials", e.message)
			} finally {
				applicationState.ready = false
			}
		}
	}
}
