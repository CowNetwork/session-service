package network.cow.session.service.database.table

/**
 * @author Benedikt Wüller
 */
object Kicks : ExecutorTable() {

    val playerId = uuid("player_id").index()
    val reason = text("reason")

}
