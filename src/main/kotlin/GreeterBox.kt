import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

data class GreetMessage(val message: String)

class GreeterBox(context: ActorContext<GreetMessage>) : AbstractBehavior<GreetMessage>(context) {
    private val greeter: ActorRef<Greet> = context.spawn(Greeter.create(), "greeter")

    companion object {
        fun create(): Behavior<GreetMessage> = Behaviors.setup { GreeterBox(it) }
    }

    override fun createReceive(): Receive<GreetMessage> =
        newReceiveBuilder()
            .onMessage(GreetMessage::class.java, this::onMessage)
            .build()

    private fun onMessage(message: GreetMessage): Behavior<GreetMessage> = this.also {
        val replayTo = context.spawn(Bot.create(3), message.message.replace(" ", ""))
        greeter.tell(Greet(message.message, replayTo))
    }

}