package it.polito.wa2.turnstileservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers


@EnableWebFluxSecurity
class WebSecurityConfig {

    @Autowired
    lateinit var customUserDetailsService: CustomUserDetailsService

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(customUserDetailsService)
        authenticationManager.setPasswordEncoder(passwordEncoder())
        return authenticationManager
    }

    @Bean
    fun authenticationWebFilter(): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(authenticationManager())
        filter.setServerAuthenticationConverter(JSONAuthenticationConverter())
        filter.setRequiresAuthenticationMatcher(
            ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login")
        )
        return filter
    }

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager
    ): SecurityWebFilterChain? {
        return http
            // Disable default security
            .csrf().disable()
            .httpBasic().disable()
            .logout().disable()
            .formLogin().disable()

            // Add custom security
            .authenticationManager(authenticationManager)

            // Manage routes security
            .authorizeExchange {
                it
                    .pathMatchers("/register").hasAnyAuthority("ADMIN", "SUPERADMIN")
                    .pathMatchers("/generateTransit").hasAuthority("TURNSTILE")
                    .pathMatchers("/login").permitAll()
            }
            .addFilterAt(JwtAuthorizationFilter(jwtUtils), SecurityWebFiltersOrder.FIRST)
            .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}