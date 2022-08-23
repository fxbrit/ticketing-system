package it.polito.wa2.ticketcatalogueservice.security

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
        return http.csrf().disable()
            .authorizeExchange {
                it
                    .pathMatchers("/admin/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
                    .pathMatchers("/shop/**").authenticated()
                    .pathMatchers("/orders/**").authenticated()
                    .pathMatchers("/tickets").permitAll()
                    .and().addFilterAt(JwtAuthorizationFilter(jwtUtils), SecurityWebFiltersOrder.FIRST)
            }.build()
    }
}