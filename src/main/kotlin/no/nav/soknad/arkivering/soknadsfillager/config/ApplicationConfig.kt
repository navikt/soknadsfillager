package no.nav.soknad.arkivering.soknadsfillager.config

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties

private val defaultPropertoes = ConfigurationMap(
	mapOf(
	"APP_VERSION" to "",
	"SRVSOKNADSFILLAGER_USERNAME" to "srvsoknadsfillager",
	"SRVSOKNADSFILLAGER_PASSWORD" to "",
	"APPLICATION_PROFILE" to ""

))
