package JwtUtils

import java.util.prefs.Preferences

object TokenStorage {
    private val prefs = Preferences.userRoot().node(this.javaClass.name)

    fun saveToken(token: String) {
        prefs.put("jwt_token", token)
    }

    fun getToken(): String? = prefs.get("jwt_token", null)

    fun clearToken() {
        prefs.remove("jwt_token")
    }
}
