package no.nav.soknad.arkivering.soknadsfillager

import org.openapitools.SpringDocConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(SpringDocConfiguration::class)
@SpringBootApplication
class SoknadsfillagerApplication

fun main(args: Array<String>) {
	runApplication<SoknadsfillagerApplication>(*args)
}
