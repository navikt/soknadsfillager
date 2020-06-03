package no.nav.soknad.arkivering.soknadsfillager.db

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import org.slf4j.LoggerFactory

class EmbeddedCredentialService(): CredentialService {
	private val log = LoggerFactory.getLogger("no.nav.soknad.arkivering.soknadsfillager.EmbeddedCredentialService")
	private val MIN_REFRESH_MARGIN = 300L // seconds

	var leaseDuration: Long = 0
	var renewCredentialsTaskData: RenewCredentialsTaskData? = null

	override suspend fun runRenewCredentialsTask(applicationState: ApplicationState) {
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
					hikariConfigMXBean.setUsername(credentials.username)
					hikariConfigMXBean.setPassword(credentials.password)
					hikariPoolMXBean.softEvictConnections()
				}
			}
			delay(MIN_REFRESH_MARGIN * 1000)
		}
	}

	override fun getNewCredentials(mountPath: String, databaseName: String, role: Role): Credentials {
		return Credentials("1234", "postgres","postgres")
	}

	override fun renewCredentialsTaskData(): RenewCredentialsTaskData? {
		return renewCredentialsTaskData
	}

	override fun setRenewCredentialsTaskData(dataSource: HikariDataSource, mountPath: String, databaseName: String, role: Role) {
		renewCredentialsTaskData = RenewCredentialsTaskData(dataSource, mountPath, databaseName, role)
	}

}
