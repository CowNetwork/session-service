package network.cow.session.service

import io.grpc.stub.StreamObserver
import network.cow.mooapis.session.v1.BanPlayerRequest
import network.cow.mooapis.session.v1.BanPlayerResponse
import network.cow.mooapis.session.v1.BlacklistPlayerRequest
import network.cow.mooapis.session.v1.BlacklistPlayerResponse
import network.cow.mooapis.session.v1.CreateSessionRequest
import network.cow.mooapis.session.v1.CreateSessionResponse
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

/**
 * @author Benedikt Wüller
 */
class SessionService : SessionServiceGrpc.SessionServiceImplBase() {

    override fun createSession(request: CreateSessionRequest?, responseObserver: StreamObserver<CreateSessionResponse>?) {
        super.createSession(request, responseObserver)
    }

    override fun stopSession(request: StopSessionRequest?, responseObserver: StreamObserver<StopSessionResponse>?) {
        super.stopSession(request, responseObserver)
    }

    override fun getSession(request: GetSessionRequest?, responseObserver: StreamObserver<GetSessionResponse>?) {
        super.getSession(request, responseObserver)
    }

    override fun kickPlayer(request: KickPlayerRequest?, responseObserver: StreamObserver<KickPlayerResponse>?) {
        super.kickPlayer(request, responseObserver)
    }

    override fun banPlayer(request: BanPlayerRequest?, responseObserver: StreamObserver<BanPlayerResponse>?) {
        super.banPlayer(request, responseObserver)
    }

    override fun getBans(request: GetBansRequest?, responseObserver: StreamObserver<GetBansResponse>?) {
        super.getBans(request, responseObserver)
    }

    override fun revokeBan(request: RevokeBanRequest?, responseObserver: StreamObserver<RevokeBanResponse>?) {
        super.revokeBan(request, responseObserver)
    }

    override fun setMaintenanceMode(request: SetMaintenanceModeRequest?, responseObserver: StreamObserver<SetMaintenanceModeResponse>?) {
        super.setMaintenanceMode(request, responseObserver)
    }

    override fun blacklistPlayer(request: BlacklistPlayerRequest?, responseObserver: StreamObserver<BlacklistPlayerResponse>?) {
        super.blacklistPlayer(request, responseObserver)
    }

    override fun revokeBlacklistPlayer(request: RevokeBlacklistPlayerRequest?, responseObserver: StreamObserver<RevokeBlacklistPlayerResponse>?) {
        super.revokeBlacklistPlayer(request, responseObserver)
    }

}
