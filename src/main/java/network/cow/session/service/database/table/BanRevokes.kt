package network.cow.session.service.database.table

import org.jetbrains.exposed.sql.jodatime.datetime

/**
 * @author Benedikt Wüller
 */
object BanRevokes : ExecutorTable("ban_revokes") {

    val ban = reference("ban_id", Bans).uniqueIndex()
    val revokedAt = datetime("revoked_at")

}
