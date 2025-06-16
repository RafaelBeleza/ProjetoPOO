package Views.General

import Services.postJson
import Views.ViewManager
import apiUrl
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
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
data class RegisterRequest(val username: String, val email: String, val password: String, val confirmPassword: String)

class Register {
    val root: Parent = VBox(16.0).apply {
        alignment = Pos.CENTER
        padding = Insets(40.0)
        style = "-fx-background-color: #1e1e1e;"

        val title = Text("Register").apply {
            fill = Color.WHITE
            font = Font.font("Segoe UI", FontWeight.BOLD, 32.0)
        }

        val username = TextField().apply {
            promptText = "Username"
            style = """
                -fx-background-color: transparent;
                -fx-border-color: #444;
                -fx-text-fill: white;
                -fx-border-radius: 5;
                -fx-padding: 10;
                -fx-prompt-text-fill: #888;
            """.trimIndent()
        }

        val email = TextField().apply {
            promptText = "Email"
            style = username.style
        }

        val password = PasswordField().apply {
            promptText = "Password"
            style = username.style
        }

        val confirmPassword = PasswordField().apply {
            promptText = "Confirm Password"
            style = username.style
        }

        val error = Label().apply {
            textFill = Color.web("#ff4d4f")
            font = Font.font("Segoe UI", FontWeight.NORMAL, 12.0)
            isVisible = false
            isManaged = false
        }

        val registerBtn = Button("Register").apply {
            style = """
                -fx-background-color: #3a86ff;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
                -fx-padding: 10 24;
            """.trimIndent()

            setOnAction {
                val request = RegisterRequest(
                    username = username.text.trim(),
                    email = email.text.trim(),
                    password = password.text.trim() ,
                    confirmPassword = confirmPassword.text.trim()
                )

                val response = postJson("$apiUrl/register", request)

                if (response != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    val jsonElement = json.parseToJsonElement(response)
                    val errorMsg = jsonElement.jsonObject["error"]?.jsonPrimitive?.contentOrNull
                    val success = jsonElement.jsonObject["success"]?.jsonPrimitive?.contentOrNull

                    if (success.toBoolean()) {
                        ViewManager.setView(Login().root)
                    } else {
                        error.text = errorMsg ?: "Unknown register error"
                        error.isVisible = true
                        error.isManaged = true
                    }
                } else {
                    println("Error with application try again later")
                }
            }
        }

        val login = Hyperlink("Login").apply {
            textFill = Color.web("#999")
            style = "-fx-font-size: 12px;"
            setOnAction {
                ViewManager.setView(Login().root)
            }
        }

        children.addAll(title, username, email, password, confirmPassword, registerBtn, login, error)
    }
}
