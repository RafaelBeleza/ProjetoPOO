package Services

import okhttp3.OkHttpClient
import okhttp3.Request

fun getJson(url: String, jwtToken: String? = null): String? {
    val client = OkHttpClient()

    val requestBuilder = Request.Builder()
        .url(url)
        .get()
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