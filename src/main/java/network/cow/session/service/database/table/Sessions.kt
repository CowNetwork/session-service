package network.cow.session.service.database.table

import network.cow.session.service.database.SessionState
import network.cow.session.service.database.SessionStopType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * @author Benedikt Wüller
 */
object Sessions : UUIDTable() {

    val playerId = uuid("player_id").index()
    val type = varchar("type", 32).index()
    val ip = varchar("ip_address", 16).index()
    val state = enumerationByName("state", 32, SessionState::class).default(SessionState.UNKNOWN).index()
    val stopCause = enumerationByName("stop_cause", 32, SessionStopType::class).default(SessionStopType.UNKNOWN).index()
    val startedAt = datetime("started_at")
    val stoppedAt = datetime("stopped_at").nullable()

}
