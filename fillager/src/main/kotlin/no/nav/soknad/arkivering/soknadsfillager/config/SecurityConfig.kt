package no.nav.soknad.arkivering.soknadsfillager.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dev | prod")
@EnableJwtTokenValidation
class SecurityConfig
