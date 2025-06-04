package Objects

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object UserTable : Table("user") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val email = varchar("email", 100)
    val password = varchar("password", 60)

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}

class User {

    var id: Int? = null
    var username: String = ""
    var email: String = ""
    var password: String = ""

    fun register() {
        try {
            val result = transaction {
                UserTable.select { UserTable.email eq this@User.email }
                    .limit(1)
                    .any()
            }

            if (result) {
                throw IllegalArgumentException("Email is already registered")
            }

            val hashedPassword = BCrypt.hashpw(this.password, BCrypt.gensalt())

            transaction {
                UserTable.insert {
                    it[UserTable.username] = this@User.username
                    it[UserTable.email] = this@User.email
                    it[UserTable.password] = hashedPassword
                }
            }
        }  catch (e: Exception) {
            throw Exception("Database Error please try again later")
        }
    }


    fun login(): Boolean {
        return try {
            val result = transaction {
                val userRow = UserTable
                    .select { UserTable.email eq this@User.email }
                    .limit(1)
                    .firstOrNull()

                userRow != null && BCrypt.checkpw(this@User.password, userRow[UserTable.password])
            }

            result
        } catch (e: Exception) {
            throw Exception("Database Error please try again later")
        }
    }

}