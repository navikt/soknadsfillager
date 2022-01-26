package no.nav.soknad.arkivering.soknadsfillager.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

	@Bean
	fun openApi(): OpenAPI = OpenAPI()
		.info(Info().title("Soknadsfillager")
			.description("A file storage to which a client can upload, retrieve and delete files.")
			.version("2.0.0")
			.contact(Contact().name("team-soknad").url("https://nav-it.slack.com/archives/C9USRUMKM"))
			.license(License().name("MIT License").url("https://github.com/navikt/soknadsfillager/blob/main/LICENSE")))
			.externalDocs(ExternalDocumentation()
				.description("Documentation of the whole archiving system")
				.url("https://github.com/navikt/archiving-infrastructure/wiki"))
}
