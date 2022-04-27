package no.nav.soknad.arkivering.soknadsfillager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SoknadsfillagerApplication

fun main(args: Array<String>) {
	runApplication<SoknadsfillagerApplication>(*args)
}
