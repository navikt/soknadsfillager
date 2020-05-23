package no.nav.soknad.arkivering.soknadsfillager.db

import com.bettercloud.vault.VaultException
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import org.slf4j.LoggerFactory
import org.springframework.cloud.context.scope.refresh.RefreshScope

private val log = LoggerFactory.getLogger("no.nav.soknad.arkivering.soknadsfillager.db")
private val refreshScope: RefreshScope  = RefreshScope()

class VaultCredentialService() {
	var leaseDuration: Long = 0
	var renewCredentialsTaskData: RenewCredentialsTaskData? = null

	suspend fun runRenewCredentialsTask(applicationState: ApplicationState) {
		delay(leaseDuration)
		while (applicationState.ready) {
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
			delay(Vault.suggestedRefreshIntervalInMillis(leaseDuration * 1000))
			refreshScope.refresh("no.nav.soknad.arkivering.soknadsfillager.config.getDataSource")
		}
	}

	fun getNewCredentials(mountPath: String, databaseName: String, role: Role): VaultCredentials {
		val path = "$mountPath/creds/$databaseName-$role"
		log.info("Getting database credentials for path '$path'")
		try {
			val response = Vault.client.logical().read(path)
			val username = checkNotNull(response.data["username"]) { "Username is not set in response from Vault" }
			val password = checkNotNull(response.data["password"]) { "Password is not set in response from Vault" }
			log.info("Got new credentials (username=$username, leaseDuration=${response.leaseDuration})")
			leaseDuration = response.leaseDuration
			return VaultCredentials(response.leaseId, username, password)
		} catch (e: VaultException) {
			when (e.httpStatusCode) {
				403 -> log.error("Vault denied permission to fetch database credentials for path '$path'", e)
				else -> log.error("Could not fetch database credentials for path '$path'", e)
			}
			throw e
		}
	}
}

data class RenewCredentialsTaskData(
	val dataSource: HikariDataSource,
	val mountPath: String,
	val databaseName: String,
	val role: Role
)

data class VaultCredentials(
	val leaseId: String,
	val username: String,
	val password: String
)
