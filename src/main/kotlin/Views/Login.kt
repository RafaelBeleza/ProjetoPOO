package Views

import Views.Main
import javafx.animation.FadeTransition
import javafx.animation.ScaleTransition
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

        val error = Label().apply {
            textFill = Color.web("#ff4d4f")
            font = Font.font("Segoe UI", FontWeight.NORMAL, 12.0)
            isVisible = false
        }

        val loginBtn = Button("Login").apply {
            text = "Login"
            style = """
                -fx-background-color: #3a86ff;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
                -fx-padding: 10 24;
            """.trimIndent()

            setOnAction {
                val email = email.text
                val password = password.text

                if (email == "admin" && password == "1234") {
                    println("Logged in as Admin")
                } else {
                    error.text = "Invalid username or password"
                    error.isVisible = true
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

        children.addAll(title, email, password, loginBtn, register, error)
    }
}




