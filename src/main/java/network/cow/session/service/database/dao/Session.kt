package network.cow.session.service.database.dao

import network.cow.session.service.database.table.Sessions
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class Session(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Session>(Sessions)
    var playerId by Sessions.playerId
    var type by Sessions.type
    var ip by Sessions.ip
    var state by Sessions.state
    var stopCause by Sessions.stopCause
    var startedAt by Sessions.startedAt
    var stoppedAt by Sessions.stoppedAt
}
