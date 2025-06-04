package Objects

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.SQLException

class Database() {

    fun createTables() {
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    fun dropTables() {
        transaction {
            SchemaUtils.drop(UserTable)
        }
    }

}