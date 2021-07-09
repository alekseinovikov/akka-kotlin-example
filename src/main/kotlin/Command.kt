import akka.actor.typed.ActorRef

sealed interface Command

data class ActionPerformed(val description: String): Command
data class GetUsers(val replyTo: ActorRef<Users>): Command
data class CreateUser(val user: User, val replyTo: ActorRef<ActionPerformed>): Command
data class DeleteUser(val name: String, val replyTo: ActorRef<ActionPerformed>)