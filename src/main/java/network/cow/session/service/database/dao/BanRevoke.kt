package network.cow.session.service.database.dao

import network.cow.session.service.database.table.BanRevokes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class BanRevoke(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<BanRevoke>(BanRevokes)
    var ban by BanRevokes.ban
    var revokedAt by BanRevokes.revokedAt
    var executorType by BanRevokes.executorType
    var executorId by BanRevokes.executorId
}
