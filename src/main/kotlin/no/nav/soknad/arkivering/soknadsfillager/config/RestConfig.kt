package no.nav.soknad.arkivering.soknadsfillager.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebSecurityConfig(private val config: AppConfiguration) : WebSecurityConfigurerAdapter() {

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
		val user = config.restConfig.fileUser
		val passord = config.restConfig.password
		auth.inMemoryAuthentication()
			.withUser(user)
			.password("{noop}$passord")
			.roles("ADMIN")
	}
}
