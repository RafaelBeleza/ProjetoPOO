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
data class UserDeleteRequest(val id: Int)

@Serializable
data class UserUpdateRoleAndRealIdRequest(val id: Int, val roleId: Int, val realId: Int?)

@Serializable
data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val realId: Int?,
    val roleId: Int
)

class UserAdmin {

    val title = Text("All Users").apply {
        fill = Color.WHITE
        font = Font.font("Segoe UI", FontWeight.BOLD, 28.0)
    }

    val tableView = TableView<UserData>().apply {
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

        columns += TableColumn<UserData, Int>("ID").apply {
            cellValueFactory = PropertyValueFactory("id")
        }

        columns += TableColumn<UserData, String>("Username").apply {
            cellValueFactory = PropertyValueFactory("username")
        }

        columns += TableColumn<UserData, String>("Email").apply {
            cellValueFactory = PropertyValueFactory("email")
        }

        columns += TableColumn<UserData, Int>("RealId").apply {
            cellValueFactory = PropertyValueFactory("realId")
        }

        columns += TableColumn<UserData, Int>("RoleId").apply {
            cellValueFactory = PropertyValueFactory("roleId")
        }

        fixedCellSize = 30.0
        prefHeightProperty().bind(
            fixedCellSizeProperty().multiply(Bindings.size(items).add(1.01))
        )

        setColumnHeaderStyle()

        setRowFactory {
            TableRow<UserData>().apply {
                setOnMouseClicked { event ->
                    if (event.clickCount == 2) {
                        val selectedUser = item
                        popUp(selectedUser)
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

        children.addAll(goBack, title, tableView)
    }

    init {
        loadClasses()
    }

    fun loadClasses() {
        Thread {
            val json = Json { ignoreUnknownKeys = true }
            val request = getJson("$apiUrl/user/getAll", jwtToken = token)

            if (request != null) {
                val userList = json.decodeFromString<List<UserData>>(request)
                Platform.runLater {
                    tableView.items.setAll(userList)
                }
            }
        }.start()
    }

    fun popUp(UserData: UserData) {
        val alert = Alert(Alert.AlertType.NONE)
        alert.title = "Edit User"
        alert.headerText = null

        val updateButton = ButtonType("Update", ButtonBar.ButtonData.OTHER)
        val deleteButton = ButtonType("Delete", ButtonBar.ButtonData.LEFT)
        val cancelButton = ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        alert.buttonTypes.setAll(updateButton, deleteButton, cancelButton)

        val idLabel = Label("ID: ${UserData.id}").apply {
            textFill = Color.web("#3a86ff")
            font = Font.font("Segoe UI", FontWeight.BOLD, 16.0)
            padding = Insets(0.0, 0.0, 10.0, 0.0)
        }

        val usernameLabel = Label("Username: ${UserData.username}").apply {
            textFill = Color.web("#3a86ff")
            font = Font.font("Segoe UI", FontWeight.BOLD, 16.0)
            padding = Insets(0.0, 0.0, 10.0, 0.0)
        }

        val emailLabel = Label("Email: ${UserData.email}").apply {
            textFill = Color.web("#3a86ff")
            font = Font.font("Segoe UI", FontWeight.BOLD, 16.0)
            padding = Insets(0.0, 0.0, 10.0, 0.0)
        }

        val roleIdField = TextField(UserData.roleId.toString()).apply {
            promptText = "Role Id"
            style = "-fx-background-color: #3a3a3a; -fx-text-fill: white;"
        }

        val realIdField = TextField(UserData.realId?.toString() ?: "").apply {
            promptText = "ReaL Id"
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
                usernameLabel,
                emailLabel,
                roleIdField,
                realIdField,
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

            val username = usernameLabel.text.trim()
            val email = emailLabel.text.trim()
            val roleId = roleIdField.text.trim()
            val realId = realIdField.text.toIntOrNull()

            if (username.isBlank() || email.isBlank() || roleId.isBlank()) {
                errorLabel.text = "Please fill in all fields with valid values."
                errorLabel.isVisible = true
                errorLabel.isManaged = true
                return@addEventFilter
            }

            val request = UserUpdateRoleAndRealIdRequest(
                id = UserData.id,
                roleId = roleId.toInt(),
                realId = realId
            )

            Thread {
                val response = postJson("$apiUrl/user/updateRoleAndRealId", request, jwtToken = token)

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

            val request = UserDeleteRequest(
                id = UserData.id,
            )

            Thread {
                val response = postJson("$apiUrl/user/delete", request, token)
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