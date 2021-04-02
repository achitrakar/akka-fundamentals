package akkafsm

import akka.actor.{Actor, ActorSystem, FSM, Props}
import akka.pattern.ask
import akkafsm.Door.{CloseDoor, Data, OpenDoor, State}

import scala.concurrent.duration.DurationInt

object Door {
  sealed trait State
  case object Init extends State
  case object Closed extends State
  case object Opened extends State

  sealed trait Data
  case object Uninitialized extends Data

  sealed trait Command
  case object Initialize
  case object OpenDoor extends Command
  case object CloseDoor extends Command
}

class Door extends FSM[State, Data]{
  import Door._

  startWith(Closed, Uninitialized)

  when(Closed) {
    case Event(CloseDoor, _) =>
      sender ! "The door is already closed!"
      stay()
    case Event(OpenDoor, _) =>
      sender ! "The door is now open."
      goto(Opened)
  }

  when(Opened) {
    case Event(OpenDoor, _) =>
      sender ! "The door is already open!"
      stay()
    case Event(CloseDoor, _) =>
      sender ! "The door is now closed."
      goto(Closed)
  }

  whenUnhandled {
    case msg =>
      sender ! (s"Unhandled message $msg in $stateName")
      stay()
  }
}

class Handler extends Actor {
  override def receive: Receive = {
    case msg: String => println(msg)
    case _ => println("Unhandled message.")
  }
}

/**
 * This example demonstrates the following:
 *   - "Tell" / "!" which is fire and forget.
 *   - "Ask" / "?" - expect response from the actor.
 *
 * Note that underlying actor uses Akka FSM.
 */
object DoorHandler extends App {
  import akka.util.Timeout

  import scala.concurrent.ExecutionContext.Implicits.global
  implicit val timeout: Timeout = Timeout(5 seconds)

  val system = ActorSystem("DoorHandler")
  val handler = system.actorOf(Props(classOf[Door]))
//  handler ! OpenDoor // Tell (fire & forget)
  val responseF = handler ? OpenDoor // Ask
  responseF.map(println)

  (handler ? OpenDoor).map(println)
  (handler ? CloseDoor).map(println)
  (handler ? OpenDoor).map(println)
  (handler ? CloseDoor).map(println)
  (handler ? OpenDoor).map(println)
  (handler ? CloseDoor).map(println)
  (handler ? CloseDoor).map(println)
  (handler ? OpenDoor).map(println)
  (handler ? "RandomMessage").map(println)

//  handler ! CloseDoor
//  handler ! OpenDoor
//  handler ! OpenDoor
//  handler ! CloseDoor

  Thread.sleep(2000)
  system.terminate()
}
