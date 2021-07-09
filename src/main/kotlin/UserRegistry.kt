import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class UserRegistry(context: ActorContext<Command>) : AbstractBehavior<Command>(context) {

    private val users = mutableListOf<User>()


    companion object {
        fun create(): Behavior<Command> = Behaviors.setup { UserRegistry(it) }
    }

    override fun createReceive(): Receive<Command> = newReceiveBuilder()
        .onMessage(GetUsers::class.java, this::onGetUsers)
        .onMessage(CreateUser::class.java, this::onCreateUser)
        .onMessage(GetUser::class.java, this::onGetUser)
        .onMessage(DeleteUser::class.java, this::onDeleteUser)
        .build()

    private fun onGetUsers(command: GetUsers): Behavior<Command> = this.also {
        command.replyTo.tell(Users(users.toList()))
    }

    private fun onCreateUser(command: CreateUser): Behavior<Command> = this.also {
        users.add(command.user)
        command.replyTo.tell(ActionPerformed("User ${command.user.name} has been created!"))
    }

    private fun onGetUser(command: GetUser): Behavior<Command> = this.also {
        users.find { it.name == command.name }
            .let { command.replyTo.tell(GetUserResponse(it)) }
    }

    private fun onDeleteUser(command: DeleteUser): Behavior<Command> = this.also {
        users.removeIf { it.name == command.name }
        command.replyTo.tell(ActionPerformed("User ${command.name} has ben deleted!"))
    }

}