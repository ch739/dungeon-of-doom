# Dungeon Of Doom

Documentation

HOW TO
WELCOME MENU

On launch the title menu will come up. There are three text field that need to be filled in:
	1. 'IP' - This is the IP of the server you are wishing to connect to.
	2. 'Port' - This is the port of the server you are wishing to connect to.
	3. 'Name' - This is the name you wish your player to have in the game.
	
If any of these details are incorrect/invalid you will be notified by a popup window.
Press Enter Game button to start the game.

GAME
The GUI is separated into separate components:
1.Game view (North West) - shows a graphical representation about what is happening in the game.
2.Controls (North East) - shows all the available in-game commands available. If you leave mouse over a command icon it will display a hint.
3.Equipment Panel(East) - shows all the currently collected treasure.
4.Command & Chat (South) - you can which between these top panels via the tab. You can enter commands manually in the command panel and it will show regular updates. The chat panel can be used to communicate with people also playing the game.
		
CHAT
To send a public message while in game enter you message in the text field in the chat panel tab
and press enter to send.

To send a private message to a player while in game enter "whisper" + the name of the player + the 
message and press enter to send. Private messages to you will be displayed in purple. You can also send emoticons over chat by using the appropriate string e.g. :) equals a smiley face.

Test plan
Area	Description	Asserts
Entering the Game	This involves whether the IP and port number entered by the user allows the player to connect to the server. Also, the transition from the title GUI to game GUI.	-Correct IP and port connects to server.
-Incorrect IP and port number doesn’t cause errors.
-When connected to server, the game GUI is displayed.
Display	This is checking that the GUI is receiving the correcting look reply and that it is also displaying the correct graphics.	-Look reply received in the DungeonDisplay class is the same as the look reply in the server.
-Graphics align to the look reply. 
-Graphics align to the render hints.
Commands (buttons)	Checking whether the buttons that contain commands.	-Commands follow protocol and are assigned to the correct button.
-Unsuccessful commands are correct and are displayed appropriately.
Chat	This involves checking that messages are sent and received and also that names are identical.	-Messages are sent and received.
-Private messages are only display to chosen player
-Appropriate response to private message sent to a non-existent player.
-Multiple players/bots cannot have the same name.
-When a player leaves the game the name becomes available.
-When a player leaves or enters the game all other players are notified.
-Appropriate emotion icons displayed. 

Server	Check for unexpected behaviour from the server.	-Messages not following protocol are dealt with appropriately.
-If the server disconnects then the client handles a clean socket close.
-GUI displays message to user if server disconnects.

Test results
Area	Results
Entering the Game	Depending on the type of an invalid ip address entered the client may take a while to process that the ip address is incorrect.
Spamming the start game button causes there to be multiple players added under the same client.
Display	The player type human appeared a lot as it was the default type. The reason being that my random player type generator produced negative results and therefore the default option was chosen.
Commands (buttons)	All worked correctly.
Chat	Occasionally I received a NullPointerException when calling a function to receive the index of the start of a row in the text pane. I overcome this by implementing a manual method.   

If the player had called themselves “SENT” “WHISPER” or “SHOUT” it causes the message to be interpreted as commands.

If the player only sends a one letter shout it return an empty shout error which is incorrect.
Server	If a player quits the game via the command line then the server also quits. This is because of implementation is failed to change from coursework 2. 
