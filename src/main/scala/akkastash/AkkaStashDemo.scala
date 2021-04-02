package akkastash

import akka.actor.{ActorSystem, FSM, Props, Stash}
import akka.pattern.ask
import akka.util.Timeout
import akkastash.ChatActor.{Connected, Connecting, ConnectionEstablished, ConnectionLost, Data, EmptyData, State}

import scala.concurrent.duration.DurationInt

object ChatActor {
  sealed trait State
  case object Connecting extends State
  case object Connected extends State

  sealed trait Data
  case object EmptyData extends Data

  sealed trait Command
  case object ConnectionEstablished extends Command
  case object ConnectionLost extends Command
}

/**
 * Sometimes an Actor becomes tempororily unavailable to handle certain messages.
 * Stash provides a mechanism to store those incoming messages somewhere safe and
 * retrieve them back later and start processing again when the Actor is in a state
 * to process such message.
 *
 * Stash them using `stash()` method and retrieve them back with the `unstashAll()`
 * method.
 */
class ChatActor extends FSM[State, Data] with Stash {
  startWith(Connecting, EmptyData)

  when (Connecting) {
    case Event("Ping", _) =>
      stash()
      stay()

    case Event(ConnectionEstablished, _) =>
      // Connection established, unstasll all message and go into the connected state.
      unstashAll()
      goto(Connected)
  }

  when (Connected) {
    case Event("Ping", _) =>
      sender ! "Pong" // send message immediately
      stay()
    case Event(ConnectionLost, _) =>
      // lost connection, go to connecting state and try reconnecting.
      println("Connection lost, attempting to connect...")
      goto(Connecting)
  }
}

object AkkaStashDemo extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout: Timeout = Timeout(5 seconds)

  val system = ActorSystem("ChatSystem")

  val chatActor1 = system.actorOf(Props(classOf[ChatActor]), "client1")
  // Send 3 Ping requests
  (chatActor1 ? "Ping").map(println) // get stashed
  (chatActor1 ? "Ping").map(println) // get stashed
  (chatActor1 ? "Ping").map(println) // get stashed

  // Once the connection is established, get reply to previous 3 ping requests.
  chatActor1 ! ConnectionEstablished // unstashAll and respond to previous "Ping" messages

  // When connection is established, respond top the ping request immediately.
  (chatActor1 ? "Ping").map(println) // response received immediately
  (chatActor1 ? "Ping").map(println) // response received immediately

  // Terminate the connection
  chatActor1 ! ConnectionLost

  // Should not reply to the ping again.
  (chatActor1 ? "Ping").map(println) // get stashed
  (chatActor1 ? "Ping").map(println) // get stashed
  (chatActor1 ? "Ping").map(println) // get stashed
  (chatActor1 ? "Ping").map(println) // get stashed

  // Establish the connection again
  chatActor1 ! ConnectionEstablished

  // Should see the reply to 4 ping messages

  Thread.sleep(3000)
  system.terminate()
}
