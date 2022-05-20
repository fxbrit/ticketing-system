package it.polito.wa2.ticketcatalogueservice.Security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@EnableWebFluxSecurity
class WebSecurityConfig {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/admin/**").hasAuthority("ADMIN")
            .pathMatchers("/tickets").permitAll()
            .anyExchange().hasAnyAuthority("CUSTOMER", "ADMIN")
            .and().addFilterAt(JwtAuthorizationFilter(jwtUtils), SecurityWebFiltersOrder.FIRST)
        return http.build()
    }
}