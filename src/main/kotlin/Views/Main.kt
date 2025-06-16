package Views

import JwtUtils.SessionManager.token
import Services.postJson
import Views.Admin.Class
import Views.Admin.GradeAdmin
import Views.Admin.LegalGuardianAdmin
import Views.Admin.RoleAdmin
import Views.Admin.StudentAdmin
import Views.Admin.StudentHasClassAdmin
import Views.Admin.TeacherAdmin
import Views.Admin.UserAdmin
import Views.Admin.UserData
import Views.General.Logout
import Views.Student.StudentHasClassStudent
import Views.Student.UserStudent
import apiUrl
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class UserGetRequest(val id: Int)

enum class Role {
    STUDENT, TEACHER, ADMIN, GUEST
}

data class RoleButton(
    val text: String,
    val viewSupplier: () -> Parent,
    val allowedRoles: Set<Role>
)

class Main {

    val subtitle = Text("Loading user info...").apply {
        fill = Color.web("#ccc")
        font = Font.font("Segoe UI", FontWeight.NORMAL, 22.0)
    }

    val buttonsContainer = FlowPane().apply {
        alignment = Pos.TOP_CENTER
        hgap = 16.0
        vgap = 16.0
        padding = Insets(10.0)
        prefWrapLength = 400.0
    }

    val root: Parent = VBox(24.0).apply {
        alignment = Pos.TOP_CENTER
        padding = Insets(40.0)
        style = "-fx-background-color: #1e1e1e;"
        children.addAll(subtitle, buttonsContainer)
    }

    val allButtons = listOf(
        RoleButton("My Classes Info", { StudentHasClassStudent().root }, setOf(Role.STUDENT, Role.TEACHER)),
        RoleButton("My User Info", { UserStudent().root }, setOf(Role.STUDENT, Role.TEACHER)),
        RoleButton("StudentHasClasses Managment", { StudentHasClassAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("User Managment", { UserAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("Teacher Managment", { TeacherAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("Grade Managment", { GradeAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("Role Managment", { RoleAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("Student Managment", { StudentAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("Legal Guardian Managment", { LegalGuardianAdmin().root }, setOf(Role.ADMIN)),
        RoleButton("Class Managment", { Class().root }, setOf(Role.ADMIN)),
        RoleButton("Logout", { Logout().root }, setOf(Role.ADMIN, Role.STUDENT, Role.TEACHER, Role.GUEST)),
        )

    init {
        loadUserInfo()
    }

    fun buttonsForRole(userRole: Role): List<RoleButton> {
        return allButtons.filter { roleButton ->
            userRole in roleButton.allowedRoles
        }
    }

    fun loadUserInfo() {
        val userId = JwtUtils.extractJwtInfo() ?: return
        Thread {
            val data = UserGetRequest(id = userId.toInt())

            val request = postJson(
                url = "$apiUrl/user/get",
                data = data,
                jwtToken = token
            )

            if (request != null) {
                val json = Json { ignoreUnknownKeys = true }
                val response = json.parseToJsonElement(request).jsonObject
                val userObject = response["user"]?.jsonObject
                val username = userObject?.get("username")?.jsonPrimitive?.contentOrNull ?: "User"
                val role = userObject?.get("role")?.jsonPrimitive?.contentOrNull ?: ""

                val userRole = when (role.lowercase()) {
                    "student" -> Role.STUDENT
                    "teacher" -> Role.TEACHER
                    "admin" -> Role.ADMIN
                    "guest" -> Role.ADMIN
                    else -> null
                }

                Platform.runLater {
                    subtitle.text = "Welcome to the School Management App, $username!"
                    if (userRole != null) {
                        showButtonsForRole(userRole)
                    } else {
                        subtitle.text = "User role unknown."
                        subtitle.fill = Color.web("#ff4d4f")
                    }
                }
            } else {
                Platform.runLater {
                    subtitle.text = "Failed to load user info"
                    subtitle.fill = Color.web("#ff4d4f")
                }
            }
        }.start()
    }

    fun showButtonsForRole(userRole: Role) {
        val buttons = buttonsForRole(userRole).map { roleButton ->
            Button(roleButton.text).apply {
                setOnAction {
                    ViewManager.setView(roleButton.viewSupplier())
                }
                prefWidth = 160.0
                prefHeight = 48.0
                style = """
                    -fx-background-color: #3a86ff;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 8;
                """.trimIndent()
            }
        }
        buttonsContainer.children.addAll(buttons)
    }
}
