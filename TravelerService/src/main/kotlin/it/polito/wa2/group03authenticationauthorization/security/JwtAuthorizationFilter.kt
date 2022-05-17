package it.polito.wa2.group03authenticationauthorization.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthorizationFilter(
        authManager: AuthenticationManager,
        private val headerName: String,
        private val jwtParser: JwtUtils,
) : BasicAuthenticationFilter(authManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

        // Extract header
        val token = request.getHeader(headerName).trim().split(" ")[1]

        // Validate JWT
        if (jwtParser.validateJwt(token)) {
            val user = jwtParser.getDetailsJwt(token)
            val authenticatedUser = UsernamePasswordAuthenticationToken(user.userId, null, mutableListOf(SimpleGrantedAuthority(user.role)))
            SecurityContextHolder.getContext().authentication = authenticatedUser
        }

        chain.doFilter(request, response)
    }
}