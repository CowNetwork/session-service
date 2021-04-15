package network.cow.session.service.database.table

import network.cow.mooapis.session.v1.Executor
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * @author Benedikt Wüller
 */
abstract class ExecutorTable(name: String = "") : UUIDTable(name) {
    val executorType = enumerationByName("executor_type", 32, Executor.Type::class).default(Executor.Type.TYPE_CUSTOM).index()
    val executorId = varchar("executor_id", 100).nullable().index()
    val executedAt = datetime("executed_at")
}
