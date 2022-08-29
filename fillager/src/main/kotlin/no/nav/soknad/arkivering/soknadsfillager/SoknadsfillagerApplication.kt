package no.nav.soknad.arkivering.soknadsfillager

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.openapitools.SpringDocConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

@Import(SpringDocConfiguration::class)
@SpringBootApplication
@ConfigurationPropertiesScan
class SoknadsfillagerApplication

	fun main(args: Array<String>) {


		runApplication<SoknadsfillagerApplication>(*args)
	}
