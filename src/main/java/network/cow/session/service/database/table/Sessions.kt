package network.cow.session.service.database.table

import network.cow.session.service.database.SessionState
import network.cow.session.service.database.SessionStopType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.Instant

/**
 * @author Benedikt Wüller
 */
object Sessions : UUIDTable() {

    val playerId = uuid("player_id").index()
    val ip = varchar("ip_address", 16).index()
    val state = enumeration("state", SessionState::class).default(SessionState.UNKNOWN).index()
    val stopCause = enumeration("stop_cause", SessionStopType::class).default(SessionStopType.UNKNOWN).index()
    val startedAt = datetime("started_at").default(DateTime.now(DateTimeZone.UTC))
    val stoppedAt = datetime("stopped_at").nullable()

}
