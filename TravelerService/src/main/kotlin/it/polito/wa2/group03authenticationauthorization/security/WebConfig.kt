package it.polito.wa2.group03authenticationauthorization.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebConfig(
        @Value("\${authorization.headerName}") private val headerName: String
)  : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/admin/**").hasAuthority("ADMIN")
            .antMatchers("/my/tickets").hasAnyAuthority("CUSTOMER", "ADMIN")
            .and()
            .addFilter(JwtAuthorizationFilter(authenticationManager(), headerName, jwtUtils))
    }
}