package network.cow.session.service.database.dao

import network.cow.session.service.database.table.SessionKicks
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class SessionKick(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SessionKick>(SessionKicks)
    var session by SessionKicks.session
    var kick by SessionKicks.kick
}
