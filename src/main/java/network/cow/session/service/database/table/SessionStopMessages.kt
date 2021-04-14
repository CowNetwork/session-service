package network.cow.session.service.database.table

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * @author Benedikt Wüller
 */
object SessionStopMessages : UUIDTable("session_stop_messages") {

    val session = reference("session_id", Sessions).uniqueIndex()
    val message = text("message")

}
