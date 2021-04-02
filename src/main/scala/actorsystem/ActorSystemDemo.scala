package actorsystem


import akka.actor.{Actor, ActorSystem, Props}

object Role {
  case object Starting
  case object Bench
}

object Player {
  def props(name: String): Props = Props(classOf[Player], name)
}

class Player(name: String) extends Actor {
  import Role._
  override def receive: Receive = {
    case Starting => println(s"$name joined the starting XI.")
    case Bench => println(s"$name is in bench.")
    case _ => println("Unhandled message")
  }
}

object ActorSystemDemo extends App{
  import actorsystem.Role._
  val system = ActorSystem("RedDevils")
  println(s"Actor System Name: ${system.name}")

  val starting = List("David", "Wan", "Harry", "Luke", "Victor", "Fred", "Scott", "Mason", "Bruno", "Rashford", "Martial")
    .foreach { player =>
      val p = system.actorOf(Props(classOf[Player], player)) // method 1 to create an actor
      p ! Starting
    }

  val substitue = List("Van", "James", "Cavani", "Henderson", "Eric").foreach { player =>
    val p = system.actorOf(Player.props(player)) // method 2 of creating an actor; using props method with the help of companion object.
    p ! Bench
  }

  Thread.sleep(3000)
  system.terminate()
}
