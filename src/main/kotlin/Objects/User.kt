package Objects

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object Users : Table("user") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val email = varchar("email", 100)
    val password = varchar("password", 60)

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}

class User(
    var username: String,
    var email: String,
    var password: String
) {
    fun create() {
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        try {
            transaction {
                Users.insert {
                    it[Users.username] = username
                    it[Users.email] = email
                    it[Users.password] = hashedPassword
                }
            }
            println("User registered successfully.")
        } catch (e: Exception) {
            println("Database error: ${e.message}")
            e.printStackTrace()
        }
    }
}