package network.cow.session.service

import network.cow.mooapis.session.v1.Executor
import network.cow.mooapis.session.v1.StateInitialized
import network.cow.mooapis.session.v1.StateStopped
import network.cow.mooapis.session.v1.StateUnknown
import network.cow.mooapis.session.v1.StopCauseBanned
import network.cow.mooapis.session.v1.StopCauseBlacklisted
import network.cow.mooapis.session.v1.StopCauseCustom
import network.cow.mooapis.session.v1.StopCauseDisconnected
import network.cow.mooapis.session.v1.StopCauseError
import network.cow.mooapis.session.v1.StopCauseKicked
import network.cow.mooapis.session.v1.StopCauseMaintenance
import network.cow.mooapis.session.v1.StopCauseUnknown
import network.cow.session.service.database.SessionState
import network.cow.session.service.database.SessionStopType
import network.cow.session.service.database.dao.Ban
import network.cow.session.service.database.dao.BanRevoke
import network.cow.session.service.database.dao.BlacklistEntry
import network.cow.session.service.database.dao.Kick
import network.cow.session.service.database.dao.Session
import network.cow.mooapis.session.v1.Ban as GrpcBan
import network.cow.mooapis.session.v1.BanRevoke as GrpcBanRevoke
import network.cow.mooapis.session.v1.BlacklistEntry as GrpcBlacklistEntry
import network.cow.mooapis.session.v1.Kick as GrpcKick
import network.cow.mooapis.session.v1.Session as GrpcSession

/**
 * @author Benedikt Wüller
 */

fun mapGrpcSession(session: Session) : GrpcSession {
    val builder = GrpcSession.newBuilder()
        .setId(session.id.toString())
        .setPlayerId(session.playerId.toString())
        .setIp(session.ip)
        .setStartedAt(session.startedAt.unix())

    session.stoppedAt?.let { builder.setStoppedAt(it.unix()) }

    when (session.state) {
        SessionState.UNKNOWN -> builder.unknown = StateUnknown.getDefaultInstance()
        SessionState.INITIALIZED -> builder.initialized = StateInitialized.getDefaultInstance()
        SessionState.STOPPED -> {
            val state = StateStopped.newBuilder()

            when (session.stopCause) {
                SessionStopType.UNKNOWN -> state.unknown = StopCauseUnknown.getDefaultInstance()
                SessionStopType.DISCONNECTED -> state.disconnected = StopCauseDisconnected.getDefaultInstance()
                SessionStopType.MAINTENANCE -> state.maintenance = StopCauseMaintenance.getDefaultInstance()
                SessionStopType.KICKED -> {
                    val kick = findKick(session) ?: error("Session stop cause is ${session.stopCause} but no kick is assigned.")
                    state.kicked = StopCauseKicked.newBuilder().setKick(mapGrpcKick(kick)).build()
                }
                SessionStopType.BANNED -> {
                    val ban = findBan(session) ?: error("Session stop cause is ${session.stopCause} but no ban is assigned.")
                    state.banned = StopCauseBanned.newBuilder().setBan(mapGrpcBan(ban)).build()
                }
                SessionStopType.BLACKLISTED -> {
                    val blacklistEntry = findBlacklistEntry(session.playerId)
                    state.blacklisted = if (blacklistEntry == null) {
                        StopCauseBlacklisted.getDefaultInstance()
                    } else {
                        StopCauseBlacklisted.newBuilder().setEntry(mapGrpcBlacklistEntry(blacklistEntry)).build()
                    }
                }
                SessionStopType.ERROR, SessionStopType.CUSTOM -> {
                    val message = findStopMessage(session) ?: error("Session stop cause is ${session.stopCause} but no message is assigned.")
                    if (session.stopCause == SessionStopType.ERROR) {
                        state.error = StopCauseError.newBuilder().setMessage(message.message).build()
                    } else {
                        state.custom = StopCauseCustom.newBuilder().setMessage(message.message).build()
                    }
                }
            }

            builder.stopped = state.build()
        }
    }

    return builder.build()
}

fun mapGrpcBan(ban: Ban) : GrpcBan {
    val builder = GrpcBan.newBuilder()
        .setId(ban.id.toString())
        .setPlayerId(ban.playerId.toString())
        .setReason(ban.reason)
        .setBannedAt(ban.executedAt.unix())
        .setDuration(ban.duration)
        .setExecutor(mapGrpcExecutor(ban.executorType, ban.executorId))

    findBanRevoke(ban)?.let { builder.setRevoke(mapGrpcBanRevoke(it)) }

    return builder.build()
}

fun mapGrpcBanRevoke(revoke: BanRevoke) = GrpcBanRevoke.newBuilder()
    .setId(revoke.id.toString())
    .setRevokedAt(revoke.executedAt.unix())
    .setExecutor(mapGrpcExecutor(revoke.executorType, revoke.executorId))
    .build()

fun mapGrpcKick(kick: Kick) = GrpcKick.newBuilder()
    .setId(kick.id.toString())
    .setPlayerId(kick.playerId.toString())
    .setReason(kick.reason)
    .setKickedAt(kick.executedAt.unix())
    .setExecutor(mapGrpcExecutor(kick.executorType, kick.executorId))
    .build()

fun mapGrpcBlacklistEntry(entry: BlacklistEntry) = GrpcBlacklistEntry.newBuilder()
    .setId(entry.id.toString())
    .setPlayerId(entry.playerId.toString())
    .setMessage(entry.message)
    .setExecutor(mapGrpcExecutor(entry.executorType, entry.executorId))
    .setActive(entry.isActive)
    .build()

fun mapGrpcExecutor(type: Executor.Type, id: String?) : Executor {
    val builder = Executor.newBuilder().setType(type)
    id?.let { builder.setId(it) }
    return builder.build()
}
