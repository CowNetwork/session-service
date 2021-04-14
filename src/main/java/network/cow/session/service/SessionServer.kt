package network.cow.session.service

import io.grpc.ServerBuilder

/**
 * @author Benedikt Wüller
 */
class SessionServer(private val port: Int) {

    private val server = ServerBuilder.forPort(port).addService(SessionService()).build()

    fun start() {
        server.start()
        println("Server started, listening on port $port.")
        Runtime.getRuntime().addShutdownHook(Thread(this::stop))
    }

    fun stop() {
        println("Stopping server...")
        server.shutdown()
        println("Server stopped.")
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 3921
    val server = SessionServer(port)
    server.start()
    server.blockUntilShutdown()
}
