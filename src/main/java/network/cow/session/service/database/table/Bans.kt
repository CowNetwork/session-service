package network.cow.session.service.database.table

/**
 * @author Benedikt Wüller
 */
object Bans : ExecutorTable() {

    val playerId = uuid("player_id").index()
    val reason = text("reason")
    val duration = long("duration")

}
