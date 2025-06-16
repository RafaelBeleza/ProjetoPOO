package Views.General

import JwtUtils.SessionManager
import JwtUtils.TokenStorage
import Views.Main
import Views.ViewManager
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

class Logout {
    val root: Parent = VBox(20.0).apply {
        alignment = Pos.CENTER
        padding = Insets(40.0)
        style = "-fx-background-color: #1e1e1e;"

        val label = Label("Are you sure you want to logout?").apply {
            textFill = Color.WHITE
            font = Font.font("Segoe UI", FontWeight.BOLD, 18.0)
        }

        val btnYes = Button("Yes").apply {
            prefWidth = 100.0
            style = """
                -fx-background-color: #ff4d4f;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
                -fx-padding: 10 24;
            """.trimIndent()
            setOnAction {
                SessionManager.token = null
                TokenStorage.clearToken()
                ViewManager.setView(Login().root)
            }
        }

        val btnNo = Button("No").apply {
            prefWidth = 100.0
            style = """
                -fx-background-color: #3a86ff;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
                -fx-padding: 10 24;
            """.trimIndent()
            setOnAction {
                ViewManager.setView(Main().root)
            }
        }

        children.addAll(label, btnYes, btnNo)
    }
}