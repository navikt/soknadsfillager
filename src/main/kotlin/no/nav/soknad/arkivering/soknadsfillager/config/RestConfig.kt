package no.nav.soknad.arkivering.soknadsfillager.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.util.*


@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebSecurityConfig(private val config: AppConfiguration) : WebSecurityConfigurerAdapter() {

	private val logger = LoggerFactory.getLogger(javaClass)

	override fun configure(http: HttpSecurity) {
		http
			.csrf().disable()
			.authorizeRequests()
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
			.antMatchers(HttpMethod.POST, "/login", "/register").permitAll()
			.antMatchers("/filer").authenticated()
			.and()
			.httpBasic()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	}

	@Autowired
	fun configureGlobal(auth: AuthenticationManagerBuilder) {
		auth.userDetailsService(inMemoryUserDetailsManager())

		logger.info("Konfigurert authenticationManager")
	}

	fun inMemoryUserDetailsManager(): InMemoryUserDetailsManager {
		val inMemoryUserDetailsManager = InMemoryUserDetailsManager()

		inMemoryUserDetailsManager.createUser(User.withUsername(config.restConfig.fileUser).password(config.restConfig.fileUserPassword).roles("USER").build())
		logger.info("Konfigurert fileUser=${config.restConfig.fileUser}")
		if (!config.restConfig.fileUser.equals(config.restConfig.fileWriter, true)) {
			logger.info("Konfigurert fileWriter=${config.restConfig.fileWriter}")
			inMemoryUserDetailsManager.createUser(User.withUsername(config.restConfig.fileWriter).password(config.restConfig.fileWriterPassword).roles("USER").build())
		}

		return inMemoryUserDetailsManager
	}

}
