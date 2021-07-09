import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors
import akka.http.javadsl.Http
import akka.http.javadsl.ServerBinding
import akka.http.javadsl.server.Route
import com.typesafe.config.ConfigFactory
import java.util.concurrent.CompletionStage

fun main(args: Array<String>) {
    val config = ConfigFactory.load()
    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    val root: Behavior<NotUsed> = Behaviors.setup {
        val userRegistryActor = it.spawn(UserRegistry.create(), UserRegistry::class.simpleName)

        startHttpServer(UserRoutes(it.system, userRegistryActor).userRoutes(), it.system, host, port)

        Behaviors.empty<NotUsed>()
    }

    ActorSystem.create(root, "UserHttpServer")

}

private fun startHttpServer(route: Route, system: ActorSystem<*>, host: String, port: Int) {
    val futureBind: CompletionStage<ServerBinding> = Http
        .get(system)
        .newServerAt(host, port)
        .bind(route)

    futureBind.whenComplete { bind, exception ->
        if (bind != null) {
            val localAddress = bind.localAddress()
            system.log().info("Server has started at: http://${localAddress.hostString}:${localAddress.port}")
        } else {
            system.log().error("Failed to bind HTTP endpoint: ", exception)
            system.terminate()
        }
    }
}