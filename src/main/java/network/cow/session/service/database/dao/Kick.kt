package network.cow.session.service.database.dao

import network.cow.session.service.database.table.Kicks
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class Kick(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Kick>(Kicks)
    var playerId by Kicks.playerId
    var reason by Kicks.reason
    var kickedAt by Kicks.kickedAt
    var executorType by Kicks.executorType
    var executorId by Kicks.executorId
}
