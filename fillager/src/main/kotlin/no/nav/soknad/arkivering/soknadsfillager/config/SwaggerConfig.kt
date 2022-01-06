package no.nav.soknad.arkivering.soknadsfillager.config

import no.nav.soknad.arkivering.soknadsfillager.api.FilesApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
@ComponentScan(basePackageClasses = [FilesApi::class])
class SwaggerConfig {
	@Bean
	fun api(): Docket = Docket(DocumentationType.OAS_30)
		.select()
		.apis(RequestHandlerSelectors.basePackage("no.nav.soknad.arkivering"))
		.paths(PathSelectors.any())
		.build()
		.apiInfo(apiInfo())

	private fun apiInfo() = ApiInfo(
		"Soknadsfillager",
		"A file storage to which a client can upload, retrieve and delete files.\n\n" +
			"[Documentation of the whole archiving system](https://github.com/navikt/archiving-infrastructure/wiki)",
		"2.0.0",
		"",
		Contact("team-soknad", "", "team-soknad@nav.no"),
		"MIT License",
		"https://github.com/navikt/soknadsfillager/blob/main/LICENSE",
		emptyList()
	)
}
