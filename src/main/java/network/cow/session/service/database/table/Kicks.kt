package network.cow.session.service.database.table

import org.jetbrains.exposed.sql.jodatime.datetime

/**
 * @author Benedikt Wüller
 */
object Kicks : ExecutorTable() {

    val playerId = uuid("player_id").index()
    val reason = text("reason")
    val kickedAt = datetime("kicked_at")

}
