package network.cow.session.service.database.dao

import network.cow.session.service.database.table.Bans
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class Ban(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Ban>(Bans)
    var playerId by Bans.playerId
    var reason by Bans.reason
    var duration by Bans.duration
    var executorType by Bans.executorType
    var executorId by Bans.executorId
    var executedAt by Bans.executedAt
}
