import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

data class Greeted(val whom: String, val from: ActorRef<Greet>)

data class Greet(val whom: String, val replyTo: ActorRef<Greeted>)

class Greeter(context: ActorContext<Greet>?) : AbstractBehavior<Greet>(context) {

    companion object {
        fun create(): Behavior<Greet> {
            return Behaviors.setup { Greeter(it) }
        }
    }

    override fun createReceive(): Receive<Greet> =
        newReceiveBuilder()
            .onMessage(Greet::class.java, this::onGreet)
            .build()


    private fun onGreet(command: Greet): Behavior<Greet?> = this.also {
        context.log.info("Hello {}!", command.whom)
        command.replyTo.tell(Greeted(command.whom, context.self))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}