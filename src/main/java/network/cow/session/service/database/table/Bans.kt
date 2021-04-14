package network.cow.session.service.database.table

import org.jetbrains.exposed.sql.jodatime.datetime

/**
 * @author Benedikt Wüller
 */
object Bans : ExecutorTable() {

    val playerId = uuid("player_id").index()
    val reason = text("reason")
    val bannedAt = datetime("banned_at")
    val duration = long("duration")

}
