package network.cow.session.service

import io.grpc.ManagedChannelBuilder
import network.cow.mooapis.user.v1.GetPlayerByIdRequest
import network.cow.mooapis.user.v1.Player
import network.cow.mooapis.user.v1.UserServiceGrpc
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
object UserServiceClient {

    private val stub: UserServiceGrpc.UserServiceBlockingStub

    init {
        val hostname = System.getenv("USER_SERVICE_HOSTNAME") ?: "127.0.0.1"
        val port = System.getenv("USER_SERVICE_PORT")?.toInt() ?: 5816

        val channel = ManagedChannelBuilder.forAddress(hostname, port).usePlaintext().build()
        this.stub = UserServiceGrpc.newBlockingStub(channel)
    }

    fun getPlayer(playerId: UUID): Player = stub.getPlayerById(GetPlayerByIdRequest.newBuilder().setPlayerId(playerId.toString()).build()).player

}
