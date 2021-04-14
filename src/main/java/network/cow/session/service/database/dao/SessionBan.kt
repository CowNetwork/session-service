package network.cow.session.service.database.dao

import network.cow.session.service.database.table.SessionBans
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class SessionBan(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SessionBan>(SessionBans)
    var session by SessionBans.session
    var ban by SessionBans.ban
}