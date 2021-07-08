import akka.actor.typed.ActorSystem

fun main(args: Array<String>) {
    val main = ActorSystem.create(GreeterBox.create(), "helloakka")

    main.tell(GreetMessage("Hello, World!"))

    try {
        readLine()
    } finally {
        main.terminate()
    }
}