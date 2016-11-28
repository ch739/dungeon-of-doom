package dod;

import java.util.Random;

import dod.game.GameLogic;

/**
 * Internal server bot, has the same implementation as the BotClient but its output is unseen
 * 
 */
public class Bot extends User {

	// The needs to know what is has since it will act like a player on the map.
	private boolean hasLantern = false;
	private boolean hasSword = false;
	private boolean hasArmour = false;

	private char[][] currentLookReply;

	/**
	 * Constructs a new instance of the Bot.
	 * 
	 * @param game The instance of the game, to run on. This is only needed for the parent class, as
	 *            the Bot sends text commands to it.
	 */
	Bot(GameLogic game) {
		super(game);
	}

	/**
	 * Controls the playing logic of the bot
	 */
	@Override
	public void run() {
		
		int i = clientTotal;
		while(!inGame){
			this.processCommand("HELLO BOT"+i);
			i++;
		}
		
		while (true) {
			
			if (usersTurn) {
				lookAtMap();
				pickupIfAvailable();
				makeRandomMove();
			}
			// Pause
			try {
				Thread.sleep(2500);
			} catch (final InterruptedException e) {
				// This will not happen with the current app
			}
		}
	}

	/**
	 * Allows the bot to receive and act on messages send from the game. For now, we just handle the
	 * LOOKREPLY and WIN.
	 * 
	 * @param the message sent by the game
	 */
	@Override
	public void outputMessage(String message) {
		if (!message.equals("")) {
			// Print the message for the benefit of a human observer
			// System.out.println(message);

			String[] lines = message.split(System.getProperty("line.separator"));
			String firstLine = lines[0];
			if (firstLine.startsWith("LOOKREPLY")) {
				handleLookReply(lines);
			} else if (firstLine.startsWith("WIN")) {
				usersTurn = false;
				try {
					Thread.sleep(2500);//human-like delay
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				processCommand("SHOUT I won the game");
				try {
					Thread.sleep(2500);//delays between message and quit
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.game.removePlayer(id);
			} else if (firstLine.startsWith("LOSE")) {
				try {
					Thread.sleep(2500);//human-like delay
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				usersTurn = false;
				this.game.removePlayer(id);
			} else if (firstLine.startsWith("FAIL")) {
				// throw new RuntimeException("Bot enterred invalid command");
			}
		}
	}

	/**
	 * Issues a LOOK to update what the bot can see. Returns when it is updated.
	 **/
	private void lookAtMap(){
		processCommand("LOOK");
	}

	/**
	 * Handles the LOOKREPLY from the game, updating the bot's array.
	 * 
	 * @param lines the lines returned as part of the LOOKREPLY
	 */
	private void handleLookReply(String[] lines) {
		// Work out what the bot can see
		final int lookReplySize = lines[1].length();
		
		this.currentLookReply = new char[lookReplySize][lookReplySize];

		for (int row = 0; row < lookReplySize; row++) {
			for (int col = 0; col < lookReplySize; col++) {
				this.currentLookReply[row][col] = lines[row + 1].charAt(col);
			}
		}
	}

	/**
	 * Picks up anything the bot is standing on, if possible
	 */
	private void pickupIfAvailable() {
		switch (getCentralSquare()) {
		// We can't pick these up if we already have them, so don't even try
			case 'A':
				if (!this.hasArmour) {
					processCommand("PICKUP");
					// We assume that this will be successful, but we could check
					// the reply from the game.
					this.hasArmour = true;
				}
				break;

			case 'L':
				if (!this.hasLantern) {
					processCommand("PICKUP");
					this.hasLantern = true;
				}
				break;

			case 'S':
				if (!this.hasSword) {
					processCommand("PICKUP");
					this.hasSword = true;
				}
				break;

			case 'G':
				processCommand("PICKUP");
				processCommand("SHOUT I got some gold");
				break;

			case 'H':
				processCommand("PICKUP");
				break;

			default:
				break;
		}
	}

	/**
	 * Makes a random move, not into a wall
	 */
	private void makeRandomMove() {
		try {
			final char dir = generateRandomMove();
			if(dir == ' '){return;}//no possible move available
			final String moveString = "MOVE " + dir;
			processCommand(moveString);

		} catch (final IllegalStateException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Return a direction to move in. Note that we do checks to see what it in the square before
	 * sending the request to move to the game logic.
	 * 
	 * @return direction in which to move
	 */
	private char generateRandomMove() {
		// First, ensure there is a move
		if (!isMovePossible()) {
				this.processCommand("SHOUT I'm trapped!");
				this.processCommand("ENDTURN");
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
	 * @return true if the bot is not encircled with walls/players, false otherwise
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