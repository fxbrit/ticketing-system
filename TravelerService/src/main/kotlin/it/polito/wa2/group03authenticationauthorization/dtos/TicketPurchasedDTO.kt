package it.polito.wa2.group03authenticationauthorization.dtos

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.group03authenticationauthorization.entities.TicketPurchased
import java.util.*
import javax.crypto.spec.SecretKeySpec


data class TicketPurchasedDTO(
    val sub: UUID?,         // Ticket ID
    val iat: Date?,         // Issued at
    val sta: Date?,         // Timestamp of start validity
    val exp: Date?,         // Timestamp of end validity
    val zid: String,        // Zone ID
    val uid: Long,          // User ID
    var jws: String?        // Encoding of ticket as JWT
)

class UnixTimestampAdapter : TypeAdapter<Date?>() {
    override fun write(out: JsonWriter, value: Date?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value.time)
    }

    override fun read(input: JsonReader?): Date? {
        return if (input == null) null else Date(input.nextLong())
    }
}

fun encodeTicketToJWT(ticketDTO: TicketPurchasedDTO, key: String): String {

    //The JWT signature algorithm we will be using to sign the token
    val signatureAlgorithm = SignatureAlgorithm.HS256
    val signingKey = SecretKeySpec(key.toByteArray(), signatureAlgorithm.jcaName)

    val dateAdapter = UnixTimestampAdapter()
    val gson = GsonBuilder().registerTypeAdapter(Date::class.java, dateAdapter).create()
    val jwtBuilder = Jwts.builder().setPayload(gson.toJson(ticketDTO)).signWith(signingKey)

    return jwtBuilder.compact()

}

fun TicketPurchased.toDTO(key: String): TicketPurchasedDTO {
    val dto = TicketPurchasedDTO(ticketId, issuedAt, startValidity, endValidity, zoneId, ticketOwner.userId, null)
    dto.jws = encodeTicketToJWT(dto, key)
    return dto
}
