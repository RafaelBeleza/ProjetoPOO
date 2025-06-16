package Services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


inline fun <reified T : Any> postJson(url: String,data: T,jwtToken: String? = null): String? {
    val client = OkHttpClient()
    val json = Json { ignoreUnknownKeys = true }
    val jsonBody = json.encodeToString(data)
    val body = jsonBody.toRequestBody("application/json".toMediaType())

    val requestBuilder = Request.Builder()
        .url(url)
        .post(body)
        .addHeader("Content-Type", "application/json")

    if (!jwtToken.isNullOrBlank()) {
        requestBuilder.addHeader("Authorization", "Bearer $jwtToken")
    }

    val request = requestBuilder.build()

    return try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                null
            } else {
                response.body?.string()
            }
        }
    } catch (e: Exception) {
        null
    }
}