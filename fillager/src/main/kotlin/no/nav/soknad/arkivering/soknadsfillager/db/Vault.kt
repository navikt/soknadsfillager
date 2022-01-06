package no.nav.soknad.arkivering.soknadsfillager.db

import com.bettercloud.vault.SslConfig
import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import com.bettercloud.vault.VaultException
import kotlinx.coroutines.delay
import no.nav.soknad.arkivering.soknadsfillager.ApplicationState
import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("no.nav.soknad.arkivering.soknadsfillager.Vault")

object Vault {
	private const val MIN_REFRESH_MARGIN = 10 * 60 * 1000L // 10 minutes
	private val vaultToken: String = System.getenv("VAULT_TOKEN")
		?: getTokenFromFile()
		?: throw RuntimeException("Neither VAULT_TOKEN or VAULT_TOKEN_PATH is set")
	val client: Vault = Vault(
		VaultConfig()
			.address(System.getenv("VAULT_ADDR") ?: "https://vault.adeo.no")
			.engineVersion(1)
			.token(vaultToken)
			.openTimeout(5)
			.readTimeout(30)
			.sslConfig(SslConfig().build())
			.build()
	)

	suspend fun renewVaultTokenTask(applicationState: ApplicationState) {
		val lookupSelf = client.auth().lookupSelf()
		if (lookupSelf.isRenewable) {
			delay(suggestedRefreshIntervalInMillis(lookupSelf.ttl * 1000))
			while (applicationState.ready) {
				try {
					log.info("Refreshing Vault token (old TTL: ${client.auth().lookupSelf().ttl} seconds)")
					val response = client.auth().renewSelf()
					log.info("Successfully refreshed Vault token (new TTL: ${client.auth().lookupSelf().ttl} seconds)")
					delay(suggestedRefreshIntervalInMillis(response.authLeaseDuration * 1000))
				} catch (e: VaultException) {
					log.error("Could not refresh the Vault token", e)
					log.warn("Attempting to refresh Vault token in 5 seconds")
					delay(5_000L)
				}
			}
		} else {
			log.warn("Vault token is not renewable")
		}
	}

	private fun getTokenFromFile(): String? =
		File(System.getenv("VAULT_TOKEN_PATH") ?: "/var/run/secrets/nais.io/vault/vault_token").let { file ->
			when (file.exists()) {
				true -> file.readText(Charsets.UTF_8).trim()
				false -> null
			}
		}

	// We should refresh tokens from Vault before they expire, so we add a MIN_REFRESH_MARGIN margin.
	// If the token is valid for less than MIN_REFRESH_MARGIN * 2, we use duration / 2 instead.
	fun suggestedRefreshIntervalInMillis(duration: Long): Long = when {
		duration < MIN_REFRESH_MARGIN * 2 -> duration / 2
		else -> duration - MIN_REFRESH_MARGIN
	}
}
