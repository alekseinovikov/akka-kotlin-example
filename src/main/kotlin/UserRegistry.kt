import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Receive

class UserRegistry(context: ActorContext<Command>) : AbstractBehavior<Command>(context) {

    companion object {
        fun create
    }

    override fun createReceive(): Receive<Command> {
        TODO("Not yet implemented")
    }
}