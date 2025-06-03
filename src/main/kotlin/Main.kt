import Views.Login
import Views.Main
import Views.ViewManager
import org.jetbrains.exposed.sql.Database
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.geometry.Rectangle2D
import javafx.scene.layout.BorderPane
import javafx.stage.StageStyle

class MainApp : Application() {
    override fun start(stage: Stage) {
        val titleBar = Main(stage)
        val loginView = Login()

        val layout = BorderPane().apply {
            top = titleBar.root
            center = loginView.root
            style = "-fx-background-color: #1e1e1e;"
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
    val database: Objects.Database = Objects.Database()

    Database.connect(
        url = dbCon,
        driver = "com.mysql.cj.jdbc.Driver",
        user = dbUsername,
        password = dbPassword
    )

    println("--------------------")
    println("1.App")
    println("2.Debug Mode")
    val chooseRunMode:Int = readln().toIntOrNull() ?: 1
    if(chooseRunMode == 1){
        database.createTables()
        Application.launch(MainApp::class.java)

    } else if (chooseRunMode == 2){
        println("--------------------")
        println("1.Apagar Base de Dados")
        val chooseDebugMode:Int = readln().toIntOrNull() ?: 1
        if (chooseDebugMode == 1){
            database.dropTables()
        }
    }
}