package network.cow.session.service.database.dao

import network.cow.session.service.database.table.SessionMessages
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class SessionMessage(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SessionMessage>(SessionMessages)
    var session by SessionMessages.session
    var message by SessionMessages.message
}
