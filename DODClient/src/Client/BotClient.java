package Client;
import java.io.IOException;

import java.net.UnknownHostException;
import java.util.Random;

import GUI.BotGUI;

/**
 * This class plays the game as a very basic bot that will just move around randomly and pick up
 * anything that it lands on.
 */
public class BotClient extends Client {

	// The needs to know what is has since it will act like a player on the map.
	private boolean hasLantern = false;
	private boolean hasSword = false;
	private boolean hasArmour = false;
	//whether its the bots turn to move or not
	private boolean usersTurn = false;

	/**
	 * Constructs a new instance of the Bot.
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	BotClient(){
		super();
		gui = new BotGUI(this);
	}

	/**
	 * Controls the playing logic of the bot
	 * 
	 * @throws IOException
	 */
	@Override
	public void run() throws IOException {
		super.run();

		//bot logic loop
		Thread botThread = new Thread(new Runnable() {
		public void run() {
			while (true) {
				if (usersTurn && currentLookReply != null) {
					try {
						pickupIfAvailable();
						makeRandomMove();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// Pause
				try {
					//seems like human delay
					Thread.sleep(2500);
				} catch (final InterruptedException e) {
				}
			}
		}
		});
		botThread.start();
	}

	/**
	 * Allows the bot to receive and act on messages send from the game.
	 * 
	 * @param the message sent by the game
	 * @throws IOException
	 */
	protected void parseMessage(String message) throws IOException {
			super.parseMessage(message);

			if (message.startsWith("STARTTURN")) {
				usersTurn = true;
				try {
					Thread.sleep(2500);//human-like delay
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			else if (message.startsWith("ENDTURN")) {
				usersTurn = false;
			}
			
			else if (message.startsWith("WIN")) {
				usersTurn = false;
				try {
					Thread.sleep(2500);//human-like delay
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				outputMessage("SHOUT I won the game");
			
			}

			else if (message.startsWith("LOSE")) {
				usersTurn = false;
			}
			
			else if (message.startsWith("FAIL")) {
			}
		
	}

	/**
	 * Picks up anything the bot is standing on, if possible
	 * 
	 * @throws IOException
	 */
	private void pickupIfAvailable() throws IOException {
		switch (getCentralSquare()) {
		// We can't pick these up if we already have them, so don't even try
			case 'A':
				if (!this.hasArmour) {
					outputMessage("PICKUP");
					// We assume that this will be successful, but we could check
					// the reply from the game.
					this.hasArmour = true;
				}
				break;

			case 'L':
				if (!this.hasLantern) {
					outputMessage("PICKUP");
					this.hasLantern = true;
				}
				break;

			case 'S':
				if (!this.hasSword) {
					outputMessage("PICKUP");
					this.hasSword = true;
				}
				break;

			case 'G':
				outputMessage("PICKUP");
				outputMessage("SHOUT I got some gold");
				break;

			case 'H':
				outputMessage("PICKUP");
				break;

			default:
				break;
		}
	}

	/**
	 * Makes a random move, not into a wall/player
	 * 
	 * @throws IOException
	 */
	private void makeRandomMove() throws IOException {
		try {
			final char dir = generateRandomMove();
			if(dir == ' '){return;}//no possible move available
			final String moveString = "MOVE " + dir;
			outputMessage(moveString);

		} catch (final IllegalStateException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Return a direction to move in. Note that we do checks to see what is in the square before
	 * sending the request to move to the game logic.
	 * 
	 * @return direction in which to move
	 * @throws IOException 
	 */
	private char generateRandomMove() throws IOException {
		// First, ensure there is a move
		if (!isMovePossible()) {
			this.outputMessage("ENDTURN");
			return ' ';
		}

		final Random random = new Random();
		while (true) {
			final int dir = (int) (random.nextFloat() * 4F);

			switch (dir) {
				case 0: // N
					if ((getSquareWithOffset(0, -1) != '#') && (getSquareWithOffset(0, -1) != 'P')){
						return 'N';
					}
					break;

				case 1: // E
					if ((getSquareWithOffset(1, 0) != '#') && (getSquareWithOffset(1, 0) != 'P')) {
						return 'E';
					}
					break;

				case 2: // S
					if ((getSquareWithOffset(0, 1) != '#')&& (getSquareWithOffset(0, 1) != 'P')) {
						return 'S';
					}
					break;

				case 3: // W
					if ((getSquareWithOffset(-1, 0) != '#') && (getSquareWithOffset(-1, 0) != 'P')){
						return 'W';
					}
					break;
			}
		}
	}

	/**
	 * Obtains the square in the centre of the LOOKREPLY, i.e. that over which the bot is standing
	 * 
	 * @return the square under the bot
	 */
	private char getCentralSquare() {
		// Return the square with 0 offset
		return getSquareWithOffset(0, 0);
	}

	/**
	 * Obtains a square in of the LOOKREPLY with an offset to the bot
	 * 
	 * @return the square corresponding to the bot and offset
	 */
	private char getSquareWithOffset(int xOffset, int yOffset) {
		final int lookReplySize = this.currentLookReply.length;
		final int lookReplyCentreIndex = lookReplySize / 2; // We rely on
		// truncation

		return this.currentLookReply[lookReplyCentreIndex + yOffset][lookReplyCentreIndex + xOffset];
	}

	/**
	 * Check if the there is a possible move from the centre of the vision field to another tile
	 * 
	 * @return true if the bot is not encircled with walls, false otherwise
	 */
	private boolean isMovePossible() {
		if (((getSquareWithOffset(-1, 0) != '#') && (getSquareWithOffset(-1, 0) != 'P')) || 
				((getSquareWithOffset(0, 1) != '#') &&(getSquareWithOffset(0, 1) != 'P'))|| 
				((getSquareWithOffset(1, 0) != '#') && (getSquareWithOffset(1, 0) != 'P')) || 
				((getSquareWithOffset(0, -1) != '#') && (getSquareWithOffset(0, -1) != 'P'))){
			return true;
		}
		return false;
	}

}
