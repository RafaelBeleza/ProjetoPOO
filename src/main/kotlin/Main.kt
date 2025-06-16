import JwtUtils.SessionManager
import JwtUtils.TokenStorage
import Views.General.Login
import Views.Main
import Views.TopBar
import Views.ViewManager
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.geometry.Rectangle2D
import javafx.scene.layout.BorderPane
import javafx.stage.StageStyle

class MainApp : Application() {
    override fun start(stage: Stage) {
        val storedToken = TokenStorage.getToken()
        if (storedToken != null) {
            SessionManager.token = storedToken
        }

        val layout: BorderPane = if (SessionManager.token != null) {
            BorderPane().apply {
                top = TopBar(stage).root
                center = Main().root
                style = "-fx-background-color: #1e1e1e;"
            }
        } else {
            BorderPane().apply {
                top = TopBar(stage).root
                center = Login().root
                style = "-fx-background-color: #1e1e1e;"
            }
        }

        ViewManager.rootLayout = layout

        val sceneWidth = 800.0
        val sceneHeight = 700.0

        stage.initStyle(StageStyle.UNDECORATED)
        stage.scene = Scene(layout, sceneWidth, sceneHeight)
        stage.title = "Projeto POO"

        val screenBounds: Rectangle2D = Screen.getPrimary().visualBounds
        stage.x = (screenBounds.width - sceneWidth) / 2 + screenBounds.minX
        stage.y = (screenBounds.height - sceneHeight) / 2 + screenBounds.minY

        stage.isAlwaysOnTop = true
        stage.show()
        stage.isAlwaysOnTop = false
    }
}

fun main(){

    Application.launch(MainApp::class.java)

}