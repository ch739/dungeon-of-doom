README
************************** COMPILING & RUNNING *********************************
COMPILING

To compile the source, please use the following command in the directory containing src and bin.

javac -d bin -cp src src/Client/*.java
javac -d bin -cp src src/GUI/*.java


RUNNING

In the bin sub-directory run:

Human client:
java Client/PlayGame

Bot client:
java Client/PlayGame -b

***************************** GAME *********************************************

WELCOME MENU

On launch the title menu will come up. There are three text field that need to be filled in:
	1. 'IP' - This is the IP of the server you are wishing to connect to.
	2. 'Port' - This is the port of the server you are wishing to connect to.
	3. 'Name' - This is the name you wish your player to have in the game.
	
If any of these details are incorrect/invalid you will be notified by a popup window.

Press Enter Game to start the game.

IN GAME

The GUI is separated into separate components:
	1.Game view (North West) - shows a graphical representation about what is
		happening in the game.
	2.Controls (North East) - shows all the available in-game commands available.
		If you leave mouse over a command icon it will display a hint.
	3.Equipment Panel(East) - shows all the currently collected treasure.
	4.Command & Chat (South) - you can which between these top panels via the tab.
		You can enter commands manually in the command panel and it will show regular
		updates. The chat panel can be used to communicate with people also playing the
		game.
		
CHAT

To send a public message while in game enter you message in the text field in the chat panel tab
and press enter to send.

To send a private message to a player while in game enter "whisper" + the name of the player + the 
message and press enter to send.

Private messages to you will be displayed in purple.

You can also send emoticons over chat by using the appropriate string e.g. :) equals a smiley face.

 
BOT MODE

If you have run the game in bot mode then you will not access to the controls or the command panel.
This is because the bot is playing the game. You can however still send messages. 



