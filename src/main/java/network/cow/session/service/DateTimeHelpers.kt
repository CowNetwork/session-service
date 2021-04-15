package network.cow.session.service

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.append
import org.jetbrains.exposed.sql.jodatime.DateColumnType
import org.joda.time.DateTime

/**
 * @author Benedikt Wüller
 */

fun now() : DateTime = DateTime.now()

fun DateTime.unix() = this.millis / 1000

class DateAdd(private val dateTime: Expression<DateTime>, private val seconds: Expression<Long>) : Function<DateTime>(DateColumnType(true)) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(dateTime, " + ", seconds, " * INTERVAL '1 second'")
    }
}
