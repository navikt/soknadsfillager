package no.nav.soknad.arkivering.soknadsfillager.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("restconfig")
class RestConfig {
	lateinit var sharedUsername: String
	lateinit var sharedPassword: String
}
