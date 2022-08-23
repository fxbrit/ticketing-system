package it.polito.wa2.turnstileservice.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import it.polito.wa2.turnstileservice.dto.UserDetails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUtils(@Value("\${jwt.key}") private val key: String) {

    private val parser: JwtParser =
        Jwts.parserBuilder().setSigningKey(Base64.getEncoder().encodeToString(key.toByteArray())).build()

    fun validateJwt(authToken: String): Boolean {
        /** JWT contains fields:
         * - "sub": id of the user, corresponds to userId
         * - "role": role of the requesting user
         * an expired timestamp will cause an exception.
         */
        try {
            val body = this.parser.parseClaimsJws(authToken).body

            val userId = body.getValue("sub").toString()
            val role = (body["roles"] as List<*>)[0].toString()

            if (userId.isBlank() || role.isBlank())
                return false

            return true

        } catch (e: Exception) {
            return false
        }
    }

    fun getDetailsJwt(authToken: String): UserDetails {
        val body = this.parser.parseClaimsJws(authToken).body

        val userId = body.getValue("sub").toString().toLong()
        val role = (body["roles"] as List<*>)[0].toString()
        return UserDetails(userId, role)
    }
}
