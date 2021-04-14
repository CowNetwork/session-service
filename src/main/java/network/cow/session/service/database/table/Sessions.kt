package network.cow.session.service.database.table

import network.cow.session.service.database.SessionState
import network.cow.session.service.database.SessionStopType
import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * @author Benedikt Wüller
 */
object Sessions : UUIDTable() {

    val playerId = uuid("player_id").index()
    val ip = varchar("ip_address", 16).index()
    val state = enumeration("state", SessionState::class).default(SessionState.UNKNOWN).index()
    val stopCause = enumeration("stop_cause", SessionStopType::class).default(SessionStopType.UNKNOWN).index()

}
