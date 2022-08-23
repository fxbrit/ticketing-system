package it.polito.wa2.turnstileservice.security

import it.polito.wa2.turnstileservice.entities.Turnstile
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(private val user: Turnstile): UserDetails {
    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
       return user.id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
       return mutableListOf(GrantedAuthority { "TURNSTILE" })
    }
}