package Views

import Objects.User
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
                error.isVisible = false
                val fields = mapOf(
                    "Username" to username.text.trim(),
                    "Email" to email.text.trim(),
                    "Password" to password.text,
                    "Confirm Password" to confirmPassword.text
                )

                val emptyField = fields.entries.find { it.value.isEmpty() }

                if (emptyField != null) {
                    error.text = "${emptyField.key} is required"
                    error.isVisible = true
                } else if (password.text != confirmPassword.text) {
                    error.text = "Passwords do not match"
                    error.isVisible = true
                } else {
                    val newUser = User().apply {
                        this.username = username.text.trim()
                        this.email = email.text.trim()
                        this.password = password.text.trim()
                    }

                    try {
                        newUser.register()
                        ViewManager.setView(Login().root)
                    } catch (ex: Exception) {
                        error.text = ex.message
                        error.isVisible = true
                    }
                }
            }
        }

        val login = Hyperlink("Create Account").apply {
            textFill = Color.web("#999")
            style = "-fx-font-size: 12px;"
            setOnAction {
                ViewManager.setView(Login().root)
            }
        }

        children.addAll(title, username, email, password, confirmPassword, registerBtn, login, error)
    }
}
