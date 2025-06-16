package Views.General

import JwtUtils.SessionManager
import JwtUtils.TokenStorage
import Services.postJson
import Views.Main
import Views.ViewManager
import apiUrl
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class LoginRequest(val email: String, val password: String)

class Login() {
    val root: Parent = VBox(16.0).apply {
        alignment = Pos.CENTER
        padding = Insets(40.0)
        style = "-fx-background-color: #1e1e1e;"

        val title = Text("Sign In").apply {
            fill = Color.WHITE
            font = Font.font("Segoe UI", FontWeight.BOLD, 32.0)
        }

        val email = TextField().apply {
            promptText = "Email"
            style = """
                -fx-background-color: transparent;
                -fx-border-color: #444;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-padding: 10;
                -fx-prompt-text-fill: #888;
            """.trimIndent()
        }

        val password = PasswordField().apply {
            promptText = "Password"
            style = email.style
        }

        val rememberMe = CheckBox("Remember Me").apply {
            textFill = Color.web("#ccc")
            font = Font.font("Segoe UI", FontWeight.NORMAL, 13.0)
            style = """
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                -fx-padding: 4 0 0 0;
                -fx-text-fill: #ccc;
                -fx-cursor: hand;
            """.trimIndent()
        }

        val error = Label().apply {
            textFill = Color.web("#ff4d4f")
            font = Font.font("Segoe UI", FontWeight.NORMAL, 14.0)
            isVisible = false
            isManaged = false
        }

        val loginBtn = Button("Login").apply {
            text = "Login"
            prefWidth = 340.0
            style = """
                -fx-background-color: #3a86ff;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
                -fx-padding: 10 24;
            """.trimIndent()

            setOnAction {
                val request = LoginRequest(
                    email = email.text.trim(),
                    password = password.text.trim()
                )

                val response = postJson("$apiUrl/login", request)

                if (response != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    val jsonElement = json.parseToJsonElement(response)
                    val errorMsg = jsonElement.jsonObject["error"]?.jsonPrimitive?.contentOrNull
                    val token = jsonElement.jsonObject["token"]?.jsonPrimitive?.contentOrNull

                    if (errorMsg == null && token != null) {
                        SessionManager.token = token

                        if (rememberMe.isSelected) {
                            TokenStorage.saveToken(token)
                        }

                        ViewManager.setView(Main().root)
                    } else {
                        error.text = errorMsg ?: "Unknown login error"
                        error.isVisible = true
                        error.isManaged = true
                    }
                } else {
                    println("Error with application try again later")
                }
            }
        }

        val register = Hyperlink("Create Account").apply {
            textFill = Color.web("#999")
            style = "-fx-font-size: 12px;"
            setOnAction {
                ViewManager.setView(Register().root)
            }
        }

        children.addAll(title, email, password, rememberMe, error, loginBtn, register)
    }
}




