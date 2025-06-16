package Views.Student

import JwtUtils.SessionManager.token
import Services.getJson
import Services.postJson
import Views.Main
import Views.UserGetRequest
import Views.ViewManager
import apiUrl
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class StudentHasClassGetClassesByStudentIdRequest(val studentId: Int)

@Serializable
data class StudentHasClassData(
    val id: Int,
    val studentName: String,
    val className: String,
    val grade: Int? = null
)

class StudentHasClassStudent {

    val backButton = Button("← Back").apply {
        font = Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14.0)
        textFill = Color.WHITE
        style = "-fx-background-color: transparent; -fx-text-fill: #3a86ff;"
        setOnAction {
            ViewManager.setView(Main().root)
        }
    }

    val title = Text("All Classes").apply {
        fill = Color.WHITE
        font = Font.font("Segoe UI", FontWeight.BOLD, 28.0)
    }

    val tableView = TableView<StudentHasClassData>().apply {
        style = """
            -fx-background-color: #2e2e2e;
            -fx-control-inner-background: #2e2e2e;
            -fx-table-cell-border-color: transparent;
            -fx-table-header-border-color: transparent;
            -fx-text-background-color: white;
            -fx-selection-bar: #3a86ff;
            -fx-selection-bar-text: white;
            -fx-border-color: #444;
            -fx-border-radius: 8;
            -fx-padding: 8;
            -fx-background-radius: 8;
        """.trimIndent()

        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN

        columns += TableColumn<StudentHasClassData, Int>("ID").apply {
            cellValueFactory = PropertyValueFactory("id")
        }
        columns += TableColumn<StudentHasClassData, String>("Student Name").apply {
            cellValueFactory = PropertyValueFactory("studentName")
        }
        columns += TableColumn<StudentHasClassData, String>("Class Name").apply {
            cellValueFactory = PropertyValueFactory("className")
        }
        columns += TableColumn<StudentHasClassData, Int?>("Grade").apply {
            cellValueFactory = PropertyValueFactory("grade")
        }

        fixedCellSize = 30.0
        prefHeightProperty().bind(fixedCellSizeProperty().multiply(Bindings.size(items).add(1.01)))

        setColumnHeaderStyle()
    }

    val root: Parent = VBox(20.0).apply {
        padding = Insets(40.0)
        alignment = Pos.TOP_CENTER
        style = "-fx-background-color: #1e1e1e;"
        children.addAll(backButton, title, tableView)
    }

    init {
        loadClasses()
    }

    fun loadClasses() {
        Thread {
            try {
                val userId = JwtUtils.extractJwtInfo()?.toIntOrNull()
                if (userId == null) {
                    println("Invalid JWT: user ID not found")
                    return@Thread
                }

                val userResponse = postJson(
                    url = "$apiUrl/user/get",
                    data = UserGetRequest(id = userId),
                    jwtToken = token
                ) ?: return@Thread

                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(userResponse).jsonObject
                val user = jsonObject["user"]?.jsonObject
                val realId = user?.get("realId")?.jsonPrimitive?.intOrNull

                if (realId == null) {
                    println("realId not found in user response")
                    return@Thread
                }

                val request = postJson(
                    url = "$apiUrl/studentHasClass/getClassesByStudentId",
                    data = StudentHasClassGetClassesByStudentIdRequest(realId),
                    jwtToken = token
                )

                if (request != null) {
                    val classList = json.decodeFromString<List<StudentHasClassData>>(request)
                    Platform.runLater {
                        tableView.items.setAll(classList)
                    }
                } else {
                    Platform.runLater {
                        println("No data returned from studentHasClass API.")
                    }
                }
            } catch (e: Exception) {
                Platform.runLater {
                    println("Error loading classes: ${e.message}")
                }
            }
        }.start()
    }

    fun TableView<*>.setColumnHeaderStyle() {
        this.skinProperty().addListener { _, _, _ ->
            Platform.runLater {
                val header = this.lookup(".column-header-background")
                header?.style = "-fx-background-color: #1e1e1e; -fx-border-color: transparent;"

                val labels = this.lookupAll(".column-header .label")
                labels.forEach {
                    it.style = "-fx-text-fill: white;" // changed from black to white
                }
            }
        }
    }
}
