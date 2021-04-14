package network.cow.session.service.database.table

import network.cow.session.service.database.ExecutorType
import network.cow.session.service.database.table.Bans.default
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * @author Benedikt Wüller
 */
abstract class ExecutorTable(name: String = "") : UUIDTable(name) {
    val executorType = enumeration("executor_type", ExecutorType::class).default(ExecutorType.CUSTOM).index()
    val executorId = varchar("executor_id", 100).nullable().index()
    val executedAt = datetime("executed_at").default(DateTime.now(DateTimeZone.UTC))
}
