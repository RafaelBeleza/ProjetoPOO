package Views.Admin

import JwtUtils.SessionManager.token
import Services.getJson
import Services.postJson
import Views.Main
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
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class StudentHasClassData(
    var id: Int,
    val studentId: Int,
    val classId: Int,
    val grade: Int?
)

@Serializable
data class StudentHasClassCreateRequest(val studentId: Int, val classId: Int, val grade: Int? = null)

@Serializable
data class StudentHasClassUpdateRequest(val id: Int, val studentId: Int, val classId: Int, val grade: Int? = null)

@Serializable
data class StudentHasClassDeleteRequest(val id: Int)

class StudentHasClassAdmin {

    val title = Text("All StudentHasClass").apply {
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

        columns += TableColumn<StudentHasClassData, Int>("StudentId").apply {
            cellValueFactory = PropertyValueFactory("studentId")
        }

        columns += TableColumn<StudentHasClassData, Int>("ClassId").apply {
            cellValueFactory = PropertyValueFactory("classId")
        }

        columns += TableColumn<StudentHasClassData, Int>("Grade").apply {
            cellValueFactory = PropertyValueFactory("grade")
        }


        fixedCellSize = 30.0
        prefHeightProperty().bind(
            fixedCellSizeProperty().multiply(Bindings.size(items).add(1.01))
        )

        setColumnHeaderStyle()

        setRowFactory {
            TableRow<StudentHasClassData>().apply {
                setOnMouseClicked { event ->
                    if (event.clickCount == 2) {
                        val selectedStudentHasClass = item
                        popUp(selectedStudentHasClass)
                    }
                }
            }
        }
    }

    val root: Parent = VBox(20.0).apply {
        padding = Insets(40.0)
        alignment = Pos.TOP_CENTER
        style = "-fx-background-color: #1e1e1e;"

        val goBack = Hyperlink().apply {
            textFill = Color.web("#f0f0f0")
            style = """
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-border-color: white;
                -fx-border-width: 2;
                -fx-border-radius: 6;
                -fx-padding: 6 12 6 12;
                -fx-background-radius: 6;
                -fx-cursor: hand;
            """.trimIndent()
            text = "<-- Back to Main"

            setOnAction {
                ViewManager.setView(Main().root)
            }
        }

        val createButton = Button("Create Teacher").apply {
            textFill = Color.WHITE
            style = """
                -fx-background-color: #3a86ff;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-background-radius: 6;
                -fx-padding: 6 12 6 12;
                -fx-cursor: hand;
            """.trimIndent()
            setOnAction {
                createPopUp()
            }
        }

        children.addAll(goBack, title, createButton, tableView)
    }

    init {
        loadClasses()
    }

    fun loadClasses() {
        Thread {
            val json = Json { ignoreUnknownKeys = true }
            val request = getJson("$apiUrl/studentHasClass/getAll", jwtToken = token)

            if (request != null) {
                val studentHasClassList = json.decodeFromString<List<StudentHasClassData>>(request)
                Platform.runLater {
                    tableView.items.setAll(studentHasClassList)
                }
            }
        }.start()
    }

    fun createPopUp() {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Create New StudentHasClass"
        alert.headerText = null

        val createButton = ButtonType("Create", ButtonBar.ButtonData.OK_DONE)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(createButton, cancelButton)

        val studentIdField = TextField().apply {
            promptText = "Student Id"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val classIdField = TextField().apply {
            promptText = "Class Id"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val gradeField = TextField().apply {
            promptText = "Grade"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val errorLabel = Label().apply {
            textFill = Color.RED
            isVisible = false
            isManaged = false
        }

        val contentBox = VBox(5.0).apply {
            children.addAll(
                studentIdField,
                classIdField,
                gradeField,
                errorLabel
            )
        }

        alert.dialogPane.content = contentBox
        alert.dialogPane.style = "-fx-background-color: #1e1e1e; -fx-padding: 20;"
        alert.dialogPane.lookupButton(createButton).style = "-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold;"
        alert.dialogPane.lookupButton(cancelButton).style = "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold;"

        val createBtnNode = alert.dialogPane.lookupButton(createButton)
        createBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()

            val studentId = studentIdField.text.trim()
            val classId = classIdField.text.trim()
            val grade = gradeField.text.trim()

            if (studentId.isBlank() || classId.isBlank()) {
                errorLabel.text = "Please fill in all fields with valid values."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = StudentHasClassCreateRequest(
                studentId = studentId.toInt(),
                classId = classId.toInt(),
                grade = grade.toIntOrNull(),
            )

            Thread {
                val response = postJson("$apiUrl/studentHasClass/create", request, jwtToken = token)

                Platform.runLater {
                    if (response != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonElement = json.parseToJsonElement(response)
                        val success = jsonElement.jsonObject["success"]?.jsonPrimitive?.booleanOrNull
                        val errorMsg = jsonElement.jsonObject["error"]?.jsonPrimitive?.contentOrNull

                        if (success == true) {
                            alert.close()
                            loadClasses()
                        } else {
                            errorLabel.text = errorMsg ?: "Unknown error."
                            errorLabel.isVisible = true
                            errorLabel.isManaged = true
                        }
                    } else {
                        errorLabel.text = "Server error. Try again later."
                        errorLabel.isVisible = true
                        errorLabel.isManaged = true
                    }
                }
            }.start()
        }

        alert.showAndWait()
    }

    fun popUp(StudentHasClassData: StudentHasClassData) {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Edit StudentHasClass"
        alert.headerText = null

        val updateButton = ButtonType("Update", ButtonBar.ButtonData.OTHER)
        val deleteButton = ButtonType("Delete", ButtonBar.ButtonData.LEFT)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(updateButton, deleteButton, cancelButton)

        val idLabel = Label("ID: ${StudentHasClassData.id}").apply {
            textFill = Color.web("#3a86ff")
            font = Font.font("Segoe UI", FontWeight.BOLD, 16.0)
            padding = Insets(0.0, 0.0, 10.0, 0.0)
        }

        val studentIdField = TextField(StudentHasClassData.studentId.toString()).apply {
            promptText = "Student Id"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val classIdField = TextField(StudentHasClassData.classId.toString()).apply {
            promptText = "Class Id"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val gradeField = TextField(StudentHasClassData.grade?.toString() ?: "").apply {
            promptText = "Grade"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val errorLabel = Label().apply {
            textFill = Color.RED
            isVisible = false
            isManaged = false
        }

        val contentBox = VBox(5.0).apply {
            children.addAll(
                idLabel,
                studentIdField,
                classIdField,
                gradeField,
                errorLabel
            )
        }

        alert.dialogPane.content = contentBox
        alert.dialogPane.style = "-fx-background-color: #1e1e1e; -fx-padding: 20;"
        alert.dialogPane.lookupButton(updateButton).style = "-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold;"
        alert.dialogPane.lookupButton(deleteButton).style = "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;"
        alert.dialogPane.lookupButton(cancelButton).style = "-fx-background-color: #000000; -fx-text-fill: white; -fx-font-weight: bold;"

        val updateBtnNode = alert.dialogPane.lookupButton(updateButton)
        updateBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()

            val studentId = studentIdField.text.trim()
            val classId = classIdField.text.trim()
            val grade = gradeField.text.trim()

            if (studentId.isBlank() || classId.isBlank()) {
                errorLabel.text = "Please fill in all fields with valid values."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = StudentHasClassUpdateRequest(
                id = StudentHasClassData.id,
                studentId = studentId.toInt(),
                classId = classId.toInt(),
                grade = grade.toIntOrNull(),
            )

            Thread {
                val response = postJson("$apiUrl/studentHasClass/update", request, jwtToken = token)
                Platform.runLater {
                    if (response != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonElement = json.parseToJsonElement(response)
                        val errorMsg = jsonElement.jsonObject["error"]?.jsonPrimitive?.contentOrNull
                        val success = jsonElement.jsonObject["success"]?.jsonPrimitive?.booleanOrNull

                        if (success == true) {
                            alert.close()
                            loadClasses()
                        } else {
                            errorLabel.text = errorMsg ?: "Unknown error."
                            errorLabel.isVisible = true
                            errorLabel.isManaged = true
                        }
                    } else {
                        errorLabel.text = "Server error. Try again later."
                        errorLabel.isVisible = true
                        errorLabel.isManaged = true
                    }
                }
            }.start()
        }

        val deleteBtnNode = alert.dialogPane.lookupButton(deleteButton)
        deleteBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()

            val request = StudentHasClassDeleteRequest(
                id = StudentHasClassData.id,
            )

            Thread {
                val response = postJson("$apiUrl/studentHasClass/delete", request, token)
                Platform.runLater {
                    alert.close()
                    loadClasses()
                }
            }.start()
        }

        val cancelBtnNode = alert.dialogPane.lookupButton(cancelButton)
        cancelBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()
            alert.close()
            loadClasses()
        }

        alert.showAndWait()
    }

    fun TableView<*>.setColumnHeaderStyle() {
        this.skinProperty().addListener { _, _, _ ->
            Platform.runLater {
                val header = this.lookup(".column-header-background")
                header?.style = "-fx-background-color: #1e1e1e; -fx-border-color: transparent;"

                val labels = this.lookupAll(".column-header .label")
                labels.forEach {
                    it.style = "-fx-text-fill: black;"
                }
            }
        }
    }
}