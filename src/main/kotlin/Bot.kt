import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class Bot(val count: Int, context: ActorContext<Greeted>) : AbstractBehavior<Greeted>(context) {

    private var counter = 0

    companion object {
        fun create(count: Int): Behavior<Greeted> = Behaviors.setup { Bot(count, it) }
    }

    override fun createReceive(): Receive<Greeted> =
        newReceiveBuilder()
            .onMessage(Greeted::class.java, this::onMessage)
            .build()

    private fun onMessage(message: Greeted): Behavior<Greeted> {
        counter++
        context.log.info("Greeting {} for {}", counter, message)

        if (counter >= count) {
            return Behaviors.stopped()
        }

        message.from.tell(Greet(message.whom, context.self))
        return this
    }
}