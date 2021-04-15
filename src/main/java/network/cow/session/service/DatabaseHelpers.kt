package network.cow.session.service

import network.cow.mooapis.session.v1.SessionIdentifier
import network.cow.session.service.database.SessionState
import network.cow.session.service.database.dao.Ban
import network.cow.session.service.database.dao.BanRevoke
import network.cow.session.service.database.dao.BlacklistEntry
import network.cow.session.service.database.dao.Kick
import network.cow.session.service.database.dao.MaintenanceMode
import network.cow.session.service.database.dao.Session
import network.cow.session.service.database.dao.SessionBan
import network.cow.session.service.database.dao.SessionKick
import network.cow.session.service.database.dao.SessionStopMessage
import network.cow.session.service.database.table.BanRevokes
import network.cow.session.service.database.table.Bans
import network.cow.session.service.database.table.BlacklistEntries
import network.cow.session.service.database.table.MaintenanceModes
import network.cow.session.service.database.table.SessionBans
import network.cow.session.service.database.table.SessionKicks
import network.cow.session.service.database.table.SessionStopMessages
import network.cow.session.service.database.table.Sessions
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import java.util.UUID

/**
 * @author Benedikt Wüller
 */

fun findActiveSession(identifier: SessionIdentifier) = when {
    identifier.hasSessionId() -> Session.findById(UUID.fromString(identifier.sessionId))
    identifier.hasPlayerId() -> getActiveSessions(UUID.fromString(identifier.playerId)).firstOrNull()
    else -> null
}

fun findActiveSession(playerId: UUID) = getActiveSessions(playerId).firstOrNull()

fun getActiveSessions(playerId: UUID) = Session.find {
    (Sessions.playerId eq playerId) and (Sessions.state neq SessionState.STOPPED)
}.orderBy(Sessions.startedAt to SortOrder.DESC)

fun getActiveSessionsByType(type: String) = Session.find { (Sessions.type eq type) and (Sessions.state neq SessionState.STOPPED) }

fun findBlacklistEntry(playerId: UUID) = BlacklistEntry.find { BlacklistEntries.playerId eq playerId }.firstOrNull()

fun findStopMessage(session: Session) = SessionStopMessage.find { SessionStopMessages.session eq session.id }.firstOrNull()

fun findKick(session: Session) : Kick? {
    val pivot = SessionKick.find { SessionKicks.session eq session.id }.firstOrNull() ?: return null
    return Kick.findById(pivot.kick)
}

fun findBan(session: Session) : Ban? {
    val pivot = SessionBan.find { SessionBans.session eq session.id }.firstOrNull() ?: return null
    return Ban.findById(pivot.ban)
}

fun findBan(id: UUID) = Ban.findById(id)

fun getAllBans(playerId: UUID) = Ban.find { Bans.playerId eq playerId }

fun findActiveBan(playerId: UUID) : Ban? {
    val now = now()
    val ban = Ban.find {
        (Bans.playerId eq playerId) and (Bans.executedAt lessEq now) and (DateAdd(Bans.executedAt, Bans.duration) greaterEq now)
    }.firstOrNull() ?: return null

    findBanRevoke(ban) ?: return ban
    return null
}

fun findBanRevoke(ban: Ban) = BanRevoke.find { BanRevokes.ban eq ban.id }.firstOrNull()

fun findMaintenanceMode(type: String) = MaintenanceMode.find { MaintenanceModes.type eq type }.firstOrNull()
