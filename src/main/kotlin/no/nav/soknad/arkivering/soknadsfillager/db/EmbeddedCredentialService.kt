package no.nav.soknad.arkivering.soknadsfillager.db

import kotlinx.coroutines.delay
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import org.slf4j.LoggerFactory

class EmbeddedCredentialService {
	private val log = LoggerFactory.getLogger("no.nav.soknad.arkivering.soknadsfillager.EmbeddedCredentialService")
	private val MIN_REFRESH_MARGIN = 300L // seconds

	var leaseDuration: Long = 0
	var renewCredentialsTaskData: RenewCredentialsTaskData? = null

	suspend fun runRenewCredentialsTask(applicationState: ApplicationState) {
		delay(leaseDuration)
		while (applicationState.ready) {
			log.info("RenewCredentials")
			renewCredentialsTaskData?.run {
				val credentials = getNewCredentials(
					mountPath,
					databaseName,
					role
				)
				dataSource.apply {
					hikariConfigMXBean.setUsername("tullOgtøys")
					hikariConfigMXBean.setPassword(credentials.password)
					hikariPoolMXBean.softEvictConnections()
				}
			}
			delay(MIN_REFRESH_MARGIN * 1000)
		}
	}

	fun getNewCredentials(mountPath: String, databaseName: String, role: Role): VaultCredentials {
		return VaultCredentials("1234", "postgres","postgres")
	}

}
