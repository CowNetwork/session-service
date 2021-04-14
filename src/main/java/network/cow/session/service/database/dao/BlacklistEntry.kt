package network.cow.session.service.database.dao

import network.cow.session.service.database.table.BlacklistEntries
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class BlacklistEntry(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BlacklistEntry>(BlacklistEntries)
    var playerId by BlacklistEntries.playerId
    var message by BlacklistEntries.message
    var executorType by BlacklistEntries.executorType
    var executorId by BlacklistEntries.executorId
}
