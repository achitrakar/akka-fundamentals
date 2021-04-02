### Running the script
#### ActorSystemDemo
```bash
> sbt "runMain actorsystem.ActorSystemDemo"
Actor System Name: RedDevils
David joined the starting XI.
Wan joined the starting XI.
Luke joined the starting XI.
Harry joined the starting XI.
Victor joined the starting XI.
Scott joined the starting XI.
Fred joined the starting XI.
Rashford joined the starting XI.
Mason joined the starting XI.
Bruno joined the starting XI.
Van is in bench.
Martial joined the starting XI.
James is in bench.
Henderson is in bench.
Cavani is in bench.
Eric is in bench.
```
#### Akka FSM
```bash
> sbt "runMain akkafsm.DoorHandler"
The door is now open.
The door is now open.
The door is now closed.
The door is now open.
The door is now closed.
The door is already open!
The door is now open.
The door is already closed!
The door is now closed.
Unhandled message Event(RandomMessage,Uninitialized) in Opened
```