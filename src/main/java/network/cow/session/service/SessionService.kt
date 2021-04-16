package network.cow.session.service

import io.grpc.Status
import io.grpc.stub.StreamObserver
import network.cow.mooapis.session.v1.BanPlayerRequest
import network.cow.mooapis.session.v1.BanPlayerResponse
import network.cow.mooapis.session.v1.BlacklistPlayerRequest
import network.cow.mooapis.session.v1.BlacklistPlayerResponse
import network.cow.mooapis.session.v1.CreateSessionRequest
import network.cow.mooapis.session.v1.CreateSessionResponse
import network.cow.mooapis.session.v1.Executor
import network.cow.mooapis.session.v1.GetBansRequest
import network.cow.mooapis.session.v1.GetBansResponse
import network.cow.mooapis.session.v1.GetSessionRequest
import network.cow.mooapis.session.v1.GetSessionResponse
import network.cow.mooapis.session.v1.KickPlayerRequest
import network.cow.mooapis.session.v1.KickPlayerResponse
import network.cow.mooapis.session.v1.RevokeBanRequest
import network.cow.mooapis.session.v1.RevokeBanResponse
import network.cow.mooapis.session.v1.RevokeBlacklistPlayerRequest
import network.cow.mooapis.session.v1.RevokeBlacklistPlayerResponse
import network.cow.mooapis.session.v1.SessionServiceGrpc
import network.cow.mooapis.session.v1.SetMaintenanceModeRequest
import network.cow.mooapis.session.v1.SetMaintenanceModeResponse
import network.cow.mooapis.session.v1.StopSessionRequest
import network.cow.mooapis.session.v1.StopSessionResponse
import network.cow.session.service.database.SessionState
import network.cow.session.service.database.SessionStopType
import network.cow.session.service.database.dao.Ban
import network.cow.session.service.database.dao.BanRevoke
import network.cow.session.service.database.dao.BlacklistEntry
import network.cow.session.service.database.dao.Kick
import network.cow.session.service.database.dao.MaintenanceMode
import network.cow.session.service.database.dao.Session
import network.cow.session.service.database.dao.SessionBan
import network.cow.session.service.database.dao.SessionKick
import network.cow.session.service.database.dao.SessionStopMessage
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class SessionService : SessionServiceGrpc.SessionServiceImplBase() {

    // TODO: input validation / error handling

    override fun createSession(request: CreateSessionRequest, responseObserver: StreamObserver<CreateSessionResponse>) {
        transaction(DatabaseService.database) {
            // Stop any active (probably broken) sessions.
            getActiveSessions(UUID.fromString(request.playerId)).forEach { stopSession(it) }

            val playerId = UUID.fromString(request.playerId)
            val type = UserServiceClient.getPlayer(playerId).referenceType

            val session = Session.new {
                this.playerId = playerId
                this.type = type
                this.ip = request.ip
                this.startedAt = now()
            }

            // Check for maintenance mode.
            findMaintenanceMode(type)?.let {
                stopSession(session, SessionStopType.MAINTENANCE)
                responseObserver.onNext(CreateSessionResponse.newBuilder().setSession(mapGrpcSession(session)).build())
                return@transaction
            }

            // Check for active blacklist entries.
            val blacklistEntry = findBlacklistEntry(playerId)
            if (blacklistEntry != null && blacklistEntry.isActive) {
                stopSession(session, SessionStopType.BLACKLISTED)
                responseObserver.onNext(CreateSessionResponse.newBuilder().setSession(mapGrpcSession(session)).build())
                return@transaction
            }

            // Check for active bans.
            val ban = findActiveBan(playerId)
            if (ban != null) {
                SessionBan.new {
                    this.session = session.id
                    this.ban = ban.id
                }

                stopSession(session, SessionStopType.BANNED)
                responseObserver.onNext(CreateSessionResponse.newBuilder().setSession(mapGrpcSession(session)).build())
                return@transaction
            }

            session.state = SessionState.INITIALIZED
            responseObserver.onNext(CreateSessionResponse.newBuilder().setSession(mapGrpcSession(session)).build())
        }

        responseObserver.onCompleted()
    }

    override fun getSession(request: GetSessionRequest, responseObserver: StreamObserver<GetSessionResponse>) {
        transaction(DatabaseService.database) {
            val session = findActiveSession(request.identifier)
            if (session == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("The session does not exist.").asRuntimeException())
                return@transaction
            }

            responseObserver.onNext(GetSessionResponse.newBuilder().setSession(mapGrpcSession(session)).build())
            responseObserver.onCompleted()
        }
    }

    override fun stopSession(request: StopSessionRequest, responseObserver: StreamObserver<StopSessionResponse>) {
        transaction(DatabaseService.database) {
            val session = findActiveSession(request.identifier)
            if (session == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("The session does not exist.").asRuntimeException())
                return@transaction
            }

            val stopCause = when (request.causeCase) {
                StopSessionRequest.CauseCase.DISCONNECTED -> SessionStopType.DISCONNECTED
                StopSessionRequest.CauseCase.ERROR -> {
                    SessionStopMessage.new {
                        this.session = session.id
                        this.message = request.error.message
                    }
                    SessionStopType.ERROR
                }
                StopSessionRequest.CauseCase.CUSTOM -> {
                    SessionStopMessage.new {
                        this.session = session.id
                        this.message = request.custom.message
                    }
                    SessionStopType.CUSTOM
                }
                else -> SessionStopType.UNKNOWN
            }

            stopSession(session, stopCause)

            responseObserver.onNext(StopSessionResponse.newBuilder().setSession(mapGrpcSession(session)).build())
            responseObserver.onCompleted()
        }
    }

    override fun kickPlayer(request: KickPlayerRequest, responseObserver: StreamObserver<KickPlayerResponse>) {
        transaction(DatabaseService.database) {
            val playerId = UUID.fromString(request.playerId)
            val session = findActiveSession(playerId)
            if (session == null) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("The player is not online.").asRuntimeException())
                return@transaction
            }

            val kick = Kick.new {
                this.playerId = playerId
                this.reason = request.reason
                this.executedAt = now()
                this.executorType = request.executor.type
                this.executorId = request.executor.id
            }

            SessionKick.new {
                this.session = session.id
                this.kick = kick.id
            }

            stopSession(session, SessionStopType.KICKED)

            responseObserver.onNext(KickPlayerResponse.newBuilder().setKick(mapGrpcKick(kick)).build())
            responseObserver.onCompleted()
        }
    }

    override fun banPlayer(request: BanPlayerRequest, responseObserver: StreamObserver<BanPlayerResponse>) {
        transaction(DatabaseService.database) {
            val playerId = UUID.fromString(request.playerId)

            val ban = Ban.new {
                this.playerId = playerId
                this.reason = request.reason
                this.duration = request.duration
                this.executedAt = now()
                this.executorType = request.executor.type
                this.executorId = request.executor.id
            }

            // Stop active session and assign ban.
            findActiveSession(playerId)?.let {
                SessionBan.new {
                    this.session = it.id
                    this.ban = ban.id
                }

                stopSession(it, SessionStopType.BANNED)
            }

            responseObserver.onNext(BanPlayerResponse.newBuilder().setBan(mapGrpcBan(ban)).build())
            responseObserver.onCompleted()
        }
    }

    override fun getBans(request: GetBansRequest, responseObserver: StreamObserver<GetBansResponse>) {
        transaction(DatabaseService.database) {
            val bans = getAllBans(UUID.fromString(request.playerId))
            responseObserver.onNext(GetBansResponse.newBuilder().addAllBans(bans.map(::mapGrpcBan)).build())
            responseObserver.onCompleted()
        }
    }

    override fun revokeBan(request: RevokeBanRequest, responseObserver: StreamObserver<RevokeBanResponse>) {
        transaction(DatabaseService.database) {
            val ban = findBan(UUID.fromString(request.id))
            if (ban == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("The ban does not exist.").asRuntimeException())
                return@transaction
            }

            if (findBanRevoke(ban) != null) {
                responseObserver.onError(Status.FAILED_PRECONDITION.withDescription("The ban is already revoked.").asRuntimeException())
                return@transaction
            }

            BanRevoke.new {
                this.ban = ban.id
                this.executedAt = now()
                this.executorType = request.executor.type
                this.executorId = request.executor.id
            }

            responseObserver.onNext(RevokeBanResponse.newBuilder().setBan(mapGrpcBan(ban)).build())
            responseObserver.onCompleted()
        }
    }

    override fun setMaintenanceMode(request: SetMaintenanceModeRequest, responseObserver: StreamObserver<SetMaintenanceModeResponse>) {
        transaction(DatabaseService.database) {
            val types = request.typesList.filter { type ->
                val maintenanceMode = findMaintenanceMode(type)
                if (maintenanceMode == null) {
                    if (!request.enabled) return@filter false
                    MaintenanceMode.new { this.type = type }

                    // Stop any active sessions with this type.
                    getActiveSessionsByType(type).forEach {
                        // TODO: check for permissions (?)
                        stopSession(it, SessionStopType.MAINTENANCE)
                    }
                } else {
                    if (request.enabled) return@filter false
                    maintenanceMode.delete()
                }
                return@filter true
            }

            // TODO: broadcast maintenance mode state changes

            responseObserver.onNext(SetMaintenanceModeResponse.newBuilder().addAllTypes(types).build())
            responseObserver.onCompleted()
        }
    }

    override fun blacklistPlayer(request: BlacklistPlayerRequest, responseObserver: StreamObserver<BlacklistPlayerResponse>) {
        transaction(DatabaseService.database) {
            val playerId = UUID.fromString(request.playerId)

            val entry = findBlacklistEntry(playerId) ?: BlacklistEntry.new {
                this.playerId = playerId
                this.message = message
                this.executedAt = now()
                this.executorType = Executor.Type.TYPE_SYSTEM
            }

            entry.isActive = true

            // Stop the current session.
            findActiveSession(playerId)?.let{ stopSession(it, SessionStopType.BLACKLISTED) }

            responseObserver.onNext(BlacklistPlayerResponse.newBuilder().setEntry(mapGrpcBlacklistEntry(entry)).build())
            responseObserver.onCompleted()
        }
    }

    override fun revokeBlacklistPlayer(request: RevokeBlacklistPlayerRequest, responseObserver: StreamObserver<RevokeBlacklistPlayerResponse>) {
        transaction(DatabaseService.database) {
            findBlacklistEntry(UUID.fromString(request.playerId))?.delete()
            responseObserver.onNext(RevokeBlacklistPlayerResponse.newBuilder().setPlayerId(request.playerId).build())
            responseObserver.onCompleted()
        }
    }

    private fun stopSession(session: Session, cause: SessionStopType? = null) {
        session.state = SessionState.STOPPED
        session.stoppedAt = now()
        cause?.let { session.stopCause = it }

        // TODO: broadcast
    }

}
