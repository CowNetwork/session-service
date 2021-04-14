package network.cow.session.service.database.table

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * @author Benedikt Wüller
 */
object SessionKicks : UUIDTable("session_kicks") {

    val session = reference("session_id", Sessions).uniqueIndex()
    val kick = reference("kick_id", Kicks).uniqueIndex()

}
