package network.cow.session.service.database.table

import network.cow.session.service.database.ExecutorType
import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * @author Benedikt Wüller
 */
abstract class ExecutorTable(name: String = "") : UUIDTable(name) {

    val executorType = enumeration("executor_type", ExecutorType::class).default(ExecutorType.CUSTOM).index()
    val executorId = varchar("executor_id", 100).index()

}
