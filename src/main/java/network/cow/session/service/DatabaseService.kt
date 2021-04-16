package network.cow.session.service

import network.cow.session.service.database.table.BanRevokes
import network.cow.session.service.database.table.Bans
import network.cow.session.service.database.table.BlacklistEntries
import network.cow.session.service.database.table.Kicks
import network.cow.session.service.database.table.MaintenanceModes
import network.cow.session.service.database.table.SessionBans
import network.cow.session.service.database.table.SessionKicks
import network.cow.session.service.database.table.SessionStopMessages
import network.cow.session.service.database.table.Sessions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * @author Benedikt Wüller
 */
object DatabaseService {

    val database: Database

    init {
        val host = System.getenv("POSTGRES_HOST") ?: "127.0.0.1:5432"
        val db = System.getenv("POSTGRES_DB") ?: "postgres"
        val username = System.getenv("POSTGRES_USERNAME") ?: "postgres"
        val password = System.getenv("POSTGRES_PASSWORD") ?: "postgres"
        val schema = System.getenv("POSTGRES_SCHEMA") ?: "public"

        this.database = Database.connect("jdbc:postgresql://$host/$db?currentSchema=$schema", "org.postgresql.Driver", username, password)

        transaction (this.database) {
            // Make sure the tables exist.
            // TODO: move to migrations
            SchemaUtils.createMissingTablesAndColumns(
                Sessions,
                SessionStopMessages,
                Kicks, SessionKicks,
                Bans, BanRevokes, SessionBans,
                BlacklistEntries,
                MaintenanceModes
            )
        }
    }

}
