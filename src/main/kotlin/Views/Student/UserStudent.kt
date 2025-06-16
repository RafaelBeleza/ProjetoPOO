package Views.Student

import JwtUtils.SessionManager.token
import Services.postJson
import Views.Main
import Views.UserGetRequest
import Views.ViewManager
import apiUrl
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class StudentGetRequest(val id: Int?)

@Serializable
data class LegalGuardianGetRequest(val id: Int)

class UserStudent {

    val backButton = Button("← Back").apply {
        font = Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14.0)
        textFill = Color.WHITE
        style = "-fx-background-color: transparent; -fx-text-fill: #3a86ff;"
        setOnAction {
            ViewManager.setView(Main().root)
        }
    }

    val titleLabel = Label("👤 User Information").apply {
        font = Font.font("Segoe UI", FontWeight.BOLD, 26.0)
        textFill = Color.web("#3a86ff")
    }

    val userIdLabel = styledLabel("ID:")
    val usernameLabel = styledLabel("Username:")
    val emailLabel = styledLabel("Email:")
    val roleLabel = styledLabel("Role:")

    val studentSectionLabel = Label("🎓 Student Details").apply {
        font = Font.font("Segoe UI", FontWeight.BOLD, 22.0)
        textFill = Color.web("#3a86ff")
    }

    val fullNameLabel = styledLabel("Name:")
    val genderLabel = styledLabel("Gender:")
    val obsLabel = styledLabel("Notes:")

    val guardianSectionLabel = Label("👪 Legal Guardian").apply {
        font = Font.font("Segoe UI", FontWeight.BOLD, 22.0)
        textFill = Color.web("#3a86ff")
    }

    val guardianNameLabel = styledLabel("Name:")
    val guardianEmailLabel = styledLabel("Email:")
    val guardianPhoneLabel = styledLabel("Phone:")

    val errorLabel = Label().apply {
        textFill = Color.web("#ff4d4f")
        font = Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14.0)
    }

    val root: Parent = VBox(18.0).apply {
        alignment = Pos.TOP_LEFT
        padding = Insets(40.0)
        style = "-fx-background-color: #1e1e1e;"
        children.addAll(
            backButton,
            titleLabel,
            userIdLabel,
            usernameLabel,
            emailLabel,
            roleLabel,
            studentSectionLabel,
            fullNameLabel,
            genderLabel,
            obsLabel,
            guardianSectionLabel,
            guardianNameLabel,
            guardianEmailLabel,
            guardianPhoneLabel,
            errorLabel
        )
    }

    init {
        loadUserData()
    }

    fun loadUserData() {
        val userId = JwtUtils.extractJwtInfo()
        if (userId == null) {
            Platform.runLater {
                errorLabel.text = "User ID not found in JWT"
            }
            return
        }

        Thread {
            val userRequest = postJson(
                url = "$apiUrl/user/get",
                data = UserGetRequest(id = userId.toInt()),
                jwtToken = token
            )

            if (userRequest != null) {
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val jsonObject = json.parseToJsonElement(userRequest).jsonObject
                    val user = jsonObject["user"]?.jsonObject

                    val username = user?.get("username")?.jsonPrimitive?.contentOrNull ?: "Unknown"
                    val email = user?.get("email")?.jsonPrimitive?.contentOrNull ?: "Not available"
                    val role = user?.get("role")?.jsonPrimitive?.contentOrNull ?: "Unknown"
                    val realId = user?.get("realId")?.jsonPrimitive?.intOrNull

                    Platform.runLater {
                        userIdLabel.text = "ID: $userId"
                        usernameLabel.text = "Username: $username"
                        emailLabel.text = "Email: $email"
                        roleLabel.text = "Role: ${role.replaceFirstChar { it.uppercase() }}"
                    }

                    if (role == "student" && realId != null) {
                        loadStudentData(realId)
                    }

                } catch (e: Exception) {
                    Platform.runLater {
                        errorLabel.text = "Error parsing user data."
                    }
                }
            } else {
                Platform.runLater {
                    errorLabel.text = "Failed to fetch user data from server"
                }
            }
        }.start()
    }

    fun loadStudentData(studentId: Int?) {
        val studentRequest = postJson(
            url = "$apiUrl/student/get",
            data = StudentGetRequest(id = studentId),
            jwtToken = token
        )

        if (studentRequest != null) {
            try {
                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(studentRequest).jsonObject
                val student = jsonObject["student"]?.jsonObject
                val firstName = student?.get("firstName")?.jsonPrimitive?.content
                val lastName = student?.get("lastName")?.jsonPrimitive?.content
                val gender = student?.get("gender")?.jsonPrimitive?.content
                val obs = student?.get("obs")?.jsonPrimitive?.contentOrNull ?: "None"
                val guardianId = student?.get("legalGuardianId")?.jsonPrimitive?.intOrNull

                Platform.runLater {
                    fullNameLabel.text = "Name: $firstName $lastName"
                    genderLabel.text = "Gender: $gender"
                    obsLabel.text = "Notes: $obs"
                }

                if (guardianId != null) {
                    loadLegalGuardianData(guardianId)
                }

            } catch (e: Exception) {
                Platform.runLater {
                    errorLabel.text = "Error parsing student data."
                }
            }
        } else {
            Platform.runLater {
                errorLabel.text = "Failed to fetch student data from server"
            }
        }
    }

    fun loadLegalGuardianData(guardianId: Int) {
        val guardianRequest = postJson(
            url = "$apiUrl/legalGuardian/get",
            data = LegalGuardianGetRequest(id = guardianId),
            jwtToken = token
        )

        if (guardianRequest != null) {
            try {
                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(guardianRequest).jsonObject
                val guardian = jsonObject["legalGuardian"]?.jsonObject

                val firstName = guardian?.get("firstName")?.jsonPrimitive?.content
                val lastName = guardian?.get("lastName")?.jsonPrimitive?.content
                val email = guardian?.get("email")?.jsonPrimitive?.contentOrNull ?: ""
                val phone = guardian?.get("phone")?.jsonPrimitive?.contentOrNull ?: ""

                Platform.runLater {
                    guardianNameLabel.text = "Name: $firstName $lastName"
                    guardianEmailLabel.text = "Email: $email"
                    guardianPhoneLabel.text = "Phone: $phone"
                }
            } catch (e: Exception) {
                Platform.runLater {
                    errorLabel.text = "Error parsing guardian data."
                }
            }
        } else {
            Platform.runLater {
                errorLabel.text = "Failed to fetch guardian data from server"
            }
        }
    }

    fun styledLabel(text: String): Label = Label(text).apply {
        font = Font.font("Segoe UI", FontWeight.NORMAL, 18.0)
        textFill = Color.web("#ffffff")
    }
}