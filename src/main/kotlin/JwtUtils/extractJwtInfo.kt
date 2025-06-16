package JwtUtils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64

fun extractJwtInfo(): String? {
    return try {
        val token = SessionManager.token ?: return null
        val parts = token.split(".")
        if (parts.size != 3) return null
        val payloadJson = String(Base64.getUrlDecoder().decode(parts[1]))
        val json = Json { ignoreUnknownKeys = true }
        val obj = json.parseToJsonElement(payloadJson).jsonObject

        obj["Id"]?.jsonPrimitive?.contentOrNull
    } catch (e: Exception) {
        null
    }
}