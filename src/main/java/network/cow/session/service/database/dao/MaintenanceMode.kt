package network.cow.session.service.database.dao

import network.cow.session.service.database.table.MaintenanceModes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class MaintenanceMode(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MaintenanceMode>(MaintenanceModes)
    var type by MaintenanceModes.type
}
