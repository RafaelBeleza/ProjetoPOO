package Views.Admin

import javafx.scene.text.Text
import kotlinx.serialization.Serializable
import JwtUtils.SessionManager.token
import Services.getJson
import Services.postJson
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import Views.Main

@Serializable
data class StudentData(
    var id: Int,
    var firstName: String,
    var lastName: String,
    var gender: String,
    var obs: String?,
    var gradeId: Int?,
    var legalGuardianId: Int?
)

@Serializable
data class StudentCreateRequest(val firstName: String, val lastName: String, val gender: String, val obs: String?, val gradeId: Int?, val legalGuardianId: Int?)

@Serializable
data class StudentUpdateRequest(val id: Int, val firstName: String, val lastName: String, val gender: String, val obs: String?, val gradeId: Int?, val legalGuardianId: Int?)

@Serializable
data class StudentDeleteRequest(val id: Int)

class StudentAdmin {

    val title = Text("All Students").apply {
        fill = Color.WHITE
        font = Font.font("Segoe UI", FontWeight.BOLD, 28.0)
    }

    val tableView = TableView<StudentData>().apply {
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

        columns += TableColumn<StudentData, Int>("ID").apply {
            cellValueFactory = PropertyValueFactory("id")
        }
        columns += TableColumn<StudentData, String>("First Name").apply {
            cellValueFactory = PropertyValueFactory("firstName")
        }
        columns += TableColumn<StudentData, String>("Last Name").apply {
            cellValueFactory = PropertyValueFactory("lastName")
        }
        columns += TableColumn<StudentData, String>("Gender").apply {
            cellValueFactory = PropertyValueFactory("gender")
        }
        columns += TableColumn<StudentData, String>("Observations").apply {
            cellValueFactory = PropertyValueFactory("obs")
        }
        columns += TableColumn<StudentData, Int>("Grade ID").apply {
            cellValueFactory = PropertyValueFactory("gradeId")
        }
        columns += TableColumn<StudentData, Int>("Legal Guardian ID").apply {
            cellValueFactory = PropertyValueFactory("legalGuardianId")
        }

        fixedCellSize = 30.0
        prefHeightProperty().bind(
            fixedCellSizeProperty().multiply(Bindings.size(items).add(1.01))
        )

        setColumnHeaderStyle()

        setRowFactory {
            TableRow<StudentData>().apply {
                setOnMouseClicked { event ->
                    if (event.clickCount == 2) {
                        val selectedStudent = item
                        popUp(selectedStudent)
                    }
                }
            }
        }
    }

    val root: Parent = VBox(20.0).apply {
        padding = Insets(40.0)
        alignment = Pos.TOP_CENTER
        style = "-fx-background-color: #1e1e1e;"

        val goBack = Hyperlink("<-- Back to Main").apply {
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
            setOnAction {
                ViewManager.setView(Main().root)
            }
        }

        val createButton = Button("Create Student").apply {
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
        loadStudents()
    }

    fun loadStudents() {
        Thread {
            val json = Json { ignoreUnknownKeys = true }
            val request = getJson("$apiUrl/student/getAll", jwtToken = token)

            if (request != null) {
                val studentList = json.decodeFromString<List<StudentData>>(request)
                Platform.runLater {
                    tableView.items.setAll(studentList)
                }
            }
        }.start()
    }

    fun createPopUp() {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Create New Student"
        alert.headerText = null

        val createButton = ButtonType("Create", ButtonBar.ButtonData.OK_DONE)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(createButton, cancelButton)

        val firstNameField = TextField().apply {
            promptText = "First Name"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val lastNameField = TextField().apply {
            promptText = "Last Name"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val genderField = TextField().apply {
            promptText = "Gender"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val obsField = TextField().apply {
            promptText = "Observations"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val gradeIdField = TextField().apply {
            promptText = "Grade ID"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val guardianIdField = TextField().apply {
            promptText = "Guardian ID"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val errorLabel = Label().apply {
            textFill = Color.RED
            isVisible = false
            isManaged = false
        }

        val contentBox = VBox(5.0).apply {
            children.addAll(
                firstNameField,
                lastNameField,
                genderField,
                obsField,
                gradeIdField,
                guardianIdField,
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

            val firstName = firstNameField.text.trim()
            val lastName= lastNameField.text.trim()
            val gender = genderField.text.trim()
            val obs = obsField.text.takeIf { it.isNotBlank() }
            val gradeId = gradeIdField.text.toIntOrNull()
            val legalGuardianId = guardianIdField.text.toIntOrNull()

            if (firstName.isBlank() || lastName.isBlank() || gender.isBlank()) {
                errorLabel.text = "Please fill in all fields."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = StudentCreateRequest(
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                obs = obs,
                gradeId = gradeId,
                legalGuardianId = legalGuardianId
            )

            Thread {
                val response = postJson("$apiUrl/student/create", request, jwtToken = token)

                Platform.runLater {
                    if (response != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonElement = json.parseToJsonElement(response)
                        val success = jsonElement.jsonObject["success"]?.jsonPrimitive?.booleanOrNull
                        val errorMsg = jsonElement.jsonObject["error"]?.jsonPrimitive?.contentOrNull

                        if (success == true) {
                            alert.close()
                            loadStudents()
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

    fun popUp(StudentData: StudentData) {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Edit Student"
        alert.headerText = null

        val updateButton = ButtonType("Update", ButtonBar.ButtonData.OTHER)
        val deleteButton = ButtonType("Delete", ButtonBar.ButtonData.LEFT)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(updateButton, deleteButton, cancelButton)

        val idLabel = Label("ID: ${StudentData.id}").apply {
            textFill = Color.web("#3a86ff")
            font = Font.font("Segoe UI", FontWeight.BOLD, 16.0)
        }

        val firstNameField = TextField(StudentData.firstName).apply {
            promptText = "First Name"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val lastNameField = TextField(StudentData.lastName).apply {
            promptText = "Last Name"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val genderField = TextField(StudentData.gender).apply {
            promptText = "Gender"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val obsField = TextField(StudentData.obs ?: "").apply {
            promptText = "Observation"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val gradeIdField = TextField(StudentData.gradeId?.toString() ?: "").apply {
            promptText = "GradeId"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val guardianIdField = TextField(StudentData.legalGuardianId?.toString() ?: "").apply {
            promptText = "GuardianId"
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
                firstNameField,
                lastNameField,
                genderField,
                obsField,
                gradeIdField,
                guardianIdField,
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

            val firstName = firstNameField.text.trim()
            val lastName= lastNameField.text.trim()
            val gender = genderField.text.trim()
            val obs = obsField.text.takeIf { it.isNotBlank() }
            val gradeId = gradeIdField.text.toIntOrNull()
            val legalGuardianId = guardianIdField.text.toIntOrNull()

            if (firstName.isBlank() || lastName.isBlank() || gender.isBlank()) {
                errorLabel.text = "Please fill in all fields."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = StudentUpdateRequest(
                id = StudentData.id,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                obs = obs,
                gradeId = gradeId,
                legalGuardianId = legalGuardianId
            )

            Thread {
                val response = postJson("$apiUrl/student/update", request, jwtToken = token)
                println(response)

                Platform.runLater {
                    if (response != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonElement = json.parseToJsonElement(response)
                        val success = jsonElement.jsonObject["success"]?.jsonPrimitive?.booleanOrNull
                        val errorMsg = jsonElement.jsonObject["error"]?.jsonPrimitive?.contentOrNull

                        if (success == true) {
                            alert.close()
                            loadStudents()
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

            val request = StudentDeleteRequest(
                id = StudentData.id
            )

            Thread {
                postJson("$apiUrl/student/delete", request, jwtToken = token)
                Platform.runLater {
                    alert.close()
                    loadStudents()
                }
            }.start()
        }

        val cancelBtnNode = alert.dialogPane.lookupButton(cancelButton)
        cancelBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()
            alert.close()
            loadStudents()
        }

        alert.showAndWait()
    }

    fun TableView<*>.setColumnHeaderStyle() {
        this.skinProperty().addListener { _, _, _ ->
            Platform.runLater {
                this.lookup(".column-header-background")?.style = "-fx-background-color: #1e1e1e; -fx-border-color: transparent;"
                this.lookupAll(".column-header .label").forEach {
                    it.style = "-fx-text-fill: black;"
                }
            }
        }
    }
}
