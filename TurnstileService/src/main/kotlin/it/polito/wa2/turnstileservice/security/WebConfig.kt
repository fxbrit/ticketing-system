package it.polito.wa2.turnstileservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${jwt.key}")
    lateinit var key: String

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
        // A ReactiveAuthenticationManager that uses a ReactiveUserDetailsService
        // to validate the provided username and password.
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(customUserDetailsService)

        // The PasswordEncoder that is used for validating the password.
        authenticationManager.setPasswordEncoder(passwordEncoder())
        return authenticationManager
    }

    @Bean
    fun authenticationResponseHandler(): AuthenticationResponseHandler {
        return AuthenticationResponseHandler(key)
    }

    @Bean
    fun authenticationWebFilter(): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(authenticationManager())
        // If the request matches the path, an attempt to convert
        // the ServerWebExchange into an Authentication is made.
        filter.setRequiresAuthenticationMatcher(
            ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login")
        )
        // Conversion done using this converter
        filter.setServerAuthenticationConverter(JSONAuthenticationConverter())

        // If conversion is successful, AuthenticationWebFilter parameter ReactiveAuthenticationManager
        // is used to perform authentication. If authentication is successful then
        // ServerAuthenticationSuccessHandler is called, ServerAuthenticationFailureHandler otherwise.
        filter.setAuthenticationSuccessHandler(authenticationResponseHandler())
        filter.setAuthenticationFailureHandler(authenticationResponseHandler())
        return filter
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            // Disable default security
            .csrf().disable()
            .httpBasic().disable()
            .logout().disable()
            .formLogin().disable()

            // Manage routes security
            .authorizeExchange {
                it
                    .pathMatchers("/register").hasAnyAuthority("ADMIN", "SUPERADMIN")
                    .pathMatchers("/generateTransit", "/ticketSignatureKey").hasAuthority("TURNSTILE")
                    .pathMatchers("/login").permitAll()
            }
            .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(JwtAuthorizationFilter(jwtUtils), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}