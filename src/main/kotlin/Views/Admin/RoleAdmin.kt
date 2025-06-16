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
data class RoleData(
    val id: Int,
    val name: String,
)

@Serializable
data class RoleDeleteRequest(val id: Int)

@Serializable
data class RoleUpdateRequest(val id: Int, val name: String)

@Serializable
data class RoleCreateRequest(val name: String)

class RoleAdmin {

    val title = Text("All Roles").apply {
        fill = Color.WHITE
        font = Font.font("Segoe UI", FontWeight.BOLD, 28.0)
    }

    val tableView = TableView<RoleData>().apply {
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

        columns += TableColumn<RoleData, Int>("ID").apply {
            cellValueFactory = PropertyValueFactory("id")
        }

        columns += TableColumn<RoleData, String>("Name").apply {
            cellValueFactory = PropertyValueFactory("name")
        }

        fixedCellSize = 30.0
        prefHeightProperty().bind(
            fixedCellSizeProperty().multiply(Bindings.size(items).add(1.01))
        )

        setColumnHeaderStyle()

        setRowFactory {
            TableRow<RoleData>().apply {
                setOnMouseClicked { event ->
                    if (event.clickCount == 2) {
                        val selectedRole = item
                        if (selectedRole != null) {
                            popUp(selectedRole)
                        }
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

        val createButton = Button("Create Role").apply {
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
        loadRoles()
    }

    fun loadRoles() {
        Thread {
            val json = Json { ignoreUnknownKeys = true }
            val response = getJson("$apiUrl/role/getAll", jwtToken = token)
            if (response != null) {
                val roles = json.decodeFromString<List<RoleData>>(response)
                Platform.runLater {
                    tableView.items.setAll(roles)
                }
            }
        }.start()
    }

    fun createPopUp() {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Create New Role"
        alert.headerText = null

        val createButton = ButtonType("Create", ButtonBar.ButtonData.OK_DONE)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(createButton, cancelButton)

        val nameField = TextField().apply {
            promptText = "Role Name"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val errorLabel = Label().apply {
            textFill = Color.RED
            isVisible = false
            isManaged = false
        }

        val contentBox = VBox(5.0).apply {
            children.addAll(
                nameField,
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

            val name = nameField.text.trim()

            if (name.isBlank()) {
                errorLabel.text = "Role name cannot be empty."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = RoleCreateRequest(
                name = name
            )

            Thread {
                val response = postJson("$apiUrl/role/create", request, jwtToken = token)

                Platform.runLater {
                    if (response != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val parsed = json.parseToJsonElement(response).jsonObject
                        val success = parsed["success"]?.jsonPrimitive?.booleanOrNull ?: false
                        val error = parsed["error"]?.jsonPrimitive?.contentOrNull

                        if (success) {
                            alert.close()
                            loadRoles()
                        } else {
                            errorLabel.text = error ?: "Unknown error."
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

    fun popUp(RoleData: RoleData) {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Edit Role"
        alert.headerText = null

        val updateButton = ButtonType("Update", ButtonBar.ButtonData.OTHER)
        val deleteButton = ButtonType("Delete", ButtonBar.ButtonData.LEFT)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(updateButton, deleteButton, cancelButton)

        val idLabel = Label("ID: ${RoleData.id}").apply {
            textFill = Color.web("#3a86ff")
            font = Font.font("Segoe UI", FontWeight.BOLD, 16.0)
        }

        val nameField = TextField(RoleData.name).apply {
            promptText = "Role Name"
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
                nameField,
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

            val name = nameField.text.trim()

            if (name.isBlank()) {
                errorLabel.text = "Please fill in all fields."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = RoleUpdateRequest(
                id = RoleData.id,
                name = name
            )

            Thread {
                val response = postJson("$apiUrl/role/update", request, jwtToken = token)

                Platform.runLater {
                    if (response != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val parsed = json.parseToJsonElement(response).jsonObject
                        val success = parsed["success"]?.jsonPrimitive?.booleanOrNull ?: false
                        val error = parsed["error"]?.jsonPrimitive?.contentOrNull

                        if (success) {
                            alert.close()
                            loadRoles()
                        } else {
                            errorLabel.text = error ?: "Unknown error."
                            errorLabel.isVisible = true
                            errorLabel.isManaged = true
                        }
                    }
                }
            }.start()
        }

        val deleteBtnNode = alert.dialogPane.lookupButton(deleteButton)
        deleteBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()

            val request = RoleDeleteRequest(
                id = RoleData.id
            )

            Thread {
                postJson("$apiUrl/role/delete", request, jwtToken = token)
                Platform.runLater {
                    alert.close()
                    loadRoles()
                }
            }.start()
        }

        val cancelBtnNode = alert.dialogPane.lookupButton(cancelButton)
        cancelBtnNode.addEventFilter(ActionEvent.ACTION) { event ->
            event.consume()
            alert.close()
            loadRoles()
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
