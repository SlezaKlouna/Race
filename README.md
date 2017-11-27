# DSCars game - Over the network version
A simple 2D car racing game written in Java 8 (Swing). Can be used by two players over the network. The server capable of handling many clients. Intended for learning purposes.

#Features
- 4 selectable cars, all with transparent backgrounds. 
- 2 selectable arenas. 
- Enhanced arena graphics, which can be turned on/off. 
- Collision strategies: 
  - Car to edge: Car stops or slows down based on collision’s angle.
  - Car to grass: Car slows down. 
  - Car to car: Cars are crashing, end of the race. 
  - Collision detection is based on visible pixels (ignoring transparent). 
- Sound effects: Car collision, car acceleration, car crash and background music. All of them can be turned on/off. 
- In-game heads up display for each player showing the current speed between 0-100. 
- Help messages about the game, controls and collisions.  
- Enhanced car movement physics, which overrides the acceleration method specified in the requirements:
  - Variable speed acceleration based on current speed, max speed and terrain. 
  - Different speed limits for reversing, forwarding and off-the-road driving. 
  - Acceleration and slowdown do not require repeated pressing of the forward/back buttons (one continuous press/release is sufficient)
-	The same applications be a server or a client (but not in the same time).
-	Communication happens through TCP sockets using serialised “Message” objects and its child object.
-	The game normally ends when two players crash. Players also got notified if one of the opponents left or the server shut down during midgame. 
-	The multithreaded server application uses a dynamic thread pool where each client session runs on a separate thread. The server does not impose a hard limit on the number of simultaneous client connections. The Author also successfully executed a test scenario whereby 8 clients connected to one server instance.
-	The client also uses a separate thread for update sending to allow unblocking the UI thread.
-	The server implements a waiting lobby where players wait for an opponent to play on the desired map (as multiple maps are available).
-	Reasonably robust error handling and event logging on both sides, with appropriate user notification.

![Main menu](/screen1.png)
![Gameplay](/screen2.png)
