package it.polito.wa2.group03authenticationauthorization.dtos

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import org.springframework.beans.factory.annotation.Value
import java.util.*
import javax.crypto.spec.SecretKeySpec


data class TicketPurchasedDTO(
    val sub: UUID?,         // Ticket ID
    val iat: Date?,         // Issued at
    val sta: Date?,         // Timestamp of start validity
    val exp: Date?,         // Timestamp of end validity
    val zid: String,        // Zone ID
    var jws: String?        // Encoding of ticket as JWT
)

@Value("\${jwt.outgoing-key}")
lateinit var key: String

//The JWT signature algorithm we will be using to sign the token
private val signatureAlgorithm = SignatureAlgorithm.HS256
private val signingKey = SecretKeySpec(key.toByteArray(), signatureAlgorithm.jcaName)

class UnixTimestampAdapter : TypeAdapter<Date?>() {
    override fun write(out: JsonWriter, value: Date?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value.time / 1000)
    }

    override fun read(input: JsonReader?): Date? {
        return if (input == null) null else Date(input.nextLong() * 1000)
    }
}

fun encodeTicketToJWT(ticketDTO: TicketPurchasedDTO): String {
    val dateAdapter = UnixTimestampAdapter()
    val gson = GsonBuilder().registerTypeAdapter(Date::class.java, dateAdapter).create()
    val jwtBuilder = Jwts.builder().setPayload(gson.toJson(ticketDTO)).signWith(signingKey)
    return jwtBuilder.compact()
}

fun TicketPurchased.toDTO(): TicketPurchasedDTO {
    val dto = TicketPurchasedDTO(ticketId, issuedAt, startValidity, endValidity, zoneId, null)
    dto.jws = encodeTicketToJWT(dto)
    return dto
}
