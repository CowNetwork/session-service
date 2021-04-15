package network.cow.session.service.database.table

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * @author Benedikt Wüller
 */
object MaintenanceModes : UUIDTable() {
    val type = varchar("type", 32).uniqueIndex()
}
