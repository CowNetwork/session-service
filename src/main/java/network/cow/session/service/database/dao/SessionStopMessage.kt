package network.cow.session.service.database.dao

import network.cow.session.service.database.table.SessionStopMessages
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class SessionStopMessage(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SessionStopMessage>(SessionStopMessages)
    var session by SessionStopMessages.session
    var message by SessionStopMessages.message
}
