package network.cow.session.service.database.table

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * @author Benedikt Wüller
 */
object SessionBans : UUIDTable("session_bans") {

    val session = reference("session_id", Sessions).uniqueIndex()
    val ban = reference("ban_id", Bans)

}
