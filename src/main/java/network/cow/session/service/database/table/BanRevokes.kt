package network.cow.session.service.database.table

/**
 * @author Benedikt Wüller
 */
object BanRevokes : ExecutorTable("ban_revokes") {

    val ban = reference("ban_id", Bans).uniqueIndex()

}
