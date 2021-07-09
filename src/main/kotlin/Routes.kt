import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.AskPattern
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Directives.*
import akka.http.javadsl.server.PathMatchers
import akka.http.javadsl.server.Route
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletionStage


class UserRoutes(private val system: ActorSystem<*>, private val userRegistryActor: ActorRef<Command>) {
    private val log = LoggerFactory.getLogger(this::class.java)!!
    private val scheduler = system.scheduler()!!
    private val askTimeout = system.settings().config().getDuration("http.timeout")!!
    private val mapper = jacksonObjectMapper()


    private fun getUser(name: String): CompletionStage<GetUserResponse> =
        AskPattern.ask(userRegistryActor, { GetUser(name, it) }, askTimeout, scheduler)


    private fun deleteUser(name: String): CompletionStage<ActionPerformed> =
        AskPattern.ask(userRegistryActor, { DeleteUser(name, it) }, askTimeout, scheduler)

    private fun createUser(user: User): CompletionStage<ActionPerformed> =
        AskPattern.ask(userRegistryActor, { CreateUser(user, it) }, askTimeout, scheduler)

    private fun getUsers(): CompletionStage<Users> =
        AskPattern.ask(userRegistryActor, { GetUsers(it) }, askTimeout, scheduler)


    fun userRoutes(): Route = pathPrefix("users") {
        concat(
            pathEndOrSingleSlash {
                concat(
                    get {
                        onSuccess(getUsers()) { complete(StatusCodes.OK, it, Jackson.marshaller(mapper)) }
                    },
                    post {
                        entity(Jackson.unmarshaller(mapper, User::class.java)) { user ->
                            onSuccess(createUser(user)) {
                                log.info("Create result: {}", it.description)
                                complete(StatusCodes.CREATED, it, Jackson.marshaller(mapper))
                            }
                        }
                    }
                )
            },

            path(PathMatchers.segment()) { name: String ->
                concat(
                    get {
                        rejectEmptyResponse {
                            onSuccess(getUser(name)) {
                                complete(StatusCodes.OK, it.user, Jackson.marshaller(mapper))
                            }
                        }
                    },
                    delete {
                        onSuccess(deleteUser(name)) {
                            log.info("Delete result: {}", it.description)
                            complete(StatusCodes.OK, it, Jackson.marshaller(mapper))
                        }
                    }
                )
            }
        )
    }

}