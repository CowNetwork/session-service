package network.cow.session.service.database.table

/**
 * @author Benedikt Wüller
 */
object BlacklistEntries : ExecutorTable("blacklist_entries") {

    val playerId = uuid("player_id").uniqueIndex()
    val message = text("message")

}
