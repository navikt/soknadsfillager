package no.nav.soknad.arkivering.soknadsfillager.db

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import org.slf4j.LoggerFactory

class EmbeddedRenewService(private val vaultCredentialService: EmbeddedCredentialService, private val applicationState: ApplicationState) {
	private val log = LoggerFactory.getLogger(javaClass)

	fun startRenewTasks() {
		log.info("renewTokenTask $applicationState")

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
