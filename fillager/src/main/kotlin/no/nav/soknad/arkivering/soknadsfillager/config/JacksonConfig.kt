package no.nav.soknad.arkivering.soknadsfillager.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.spring.web.json.JacksonModuleRegistrar

@Configuration
class DateTimeSerialization : JacksonModuleRegistrar {
	override fun maybeRegisterModule(objectMapper: ObjectMapper) {
		objectMapper.registerModule(JavaTimeModule())
	}

	@Bean
	fun dateSerialization() = DateTimeSerialization()
}
