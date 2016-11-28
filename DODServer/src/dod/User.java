package dod;

import dod.game.CommandException;
import dod.game.CompassDirection;
import dod.game.GameLogic;
import dod.game.MessageException;
import dod.game.PlayerListener;
/**
 * An abstract class inherited by ClientThread and Bot. The purpose being
 * a collection of common functionality that both classes will need to avoid
 * repetitive code.
 * @author charlie
 *
 */
public abstract class User implements PlayerListener, Runnable {
	// The game which the command line user will operate on;
	protected GameLogic game;
	//unique identification number
	protected int id;
	//total number of clients
	protected static Integer clientTotal = 0;
	//this is used to check the HELLO command
	private boolean firstCommandTaken = false;
	protected boolean usersTurn = false;
	protected boolean inGame = false;

	public User(GameLogic game) {
		this.game = game;
		//prevent duplicate id's
		synchronized (clientTotal) {
			clientTotal++;
			id = clientTotal;
		}
	}

	@Override
	public abstract void run();

	/**
	 * Sends a message to the player from the game.
	 * 
	 * @ param message The message to be sent
	 */
	@Override
	public void sendMessage(String message) {
		outputMessage(message);
	}

	/**
	 * informs the user of the goal amount of gold for the map.
	 * @param n goal amount
	 */
	public void setGoal(int n){
		outputMessage("GOAL " + n);
	}
	/**
	 * Informs the user of the beginning of a player's turn.
	 */
	@Override
	public void startTurn() {
		usersTurn = true;
		outputMessage("STARTTURN");
	}

	/**
	 * Informs the user of the end of a player's turn
	 */
	@Override
	public void endTurn() {
		usersTurn = false;
		outputMessage("ENDTURN");
	}

	/**
	 * Informs the user that the player has won
	 */
	@Override
	public void win() {
		outputMessage("WIN");
	}

	/**
	 * Informs the user that the player has lost
	 */
	@Override
	public void lose() {
		outputMessage("LOSE");
	}
	
	
	/**
	 * Informs the user that the game has updated.
	 */
	@Override
	public void gameChange(String string) {
		outputMessage("CHANGE " + string);
	}

	/**
	 * Informs the user that the player's hit points have been changed
	 */
	@Override
	public void hpChange(int value) {
		outputMessage("HITMOD " + value);
	}

	/**
	 * Informs the user that the player's gold count has changed
	 */
	@Override
	public void treasureChange(int value) {
		outputMessage("TREASUREMOD " + value);
	}
	
	/**
	 * Informs the user that the equipment has changed
	 */
	@Override
	public void equipmentChange(String equipment) {
		outputMessage("EQUIP " + equipment);
	}

	/**
	 * Processes a text command from the user.
	 * 
	 * @param commandString the string containing the command and any argument
	 */
	protected synchronized final void processCommand(String commandString) {

		// Process the command string e.g. MOVE N
		final String commandStringSplit[] = commandString.split(" ", 2);
		final String command = commandStringSplit[0].toUpperCase();
		final String arg = ((commandStringSplit.length == 2) ? commandStringSplit[1] : null);

		try {
			processCommandAndArgument(command, arg);
			// only changed on first successful command.
			if (!firstCommandTaken) {
				firstCommandTaken = true;
			}
		} catch (final CommandException e) {
			outputMessage("FAIL " + e.getMessage());
		}	catch (final MessageException e) {
			outputMessage("MESSAGEFAIL " + e.getMessage());
		}
	}

	/**
	 * Processes the command and an optional argument
	 * 
	 * @param command the text command
	 * @param arg the text argument (null if no argument)
	 * @throws CommandException
	 * @throws MessageException 
	 */
	protected synchronized void processCommandAndArgument(String command, String arg) throws CommandException, MessageException {
		
		if (command.startsWith("HELLO")) {
			if (arg == null) {
				throw new CommandException("HELLO needs an argument.");
			}


			if (firstCommandTaken) {
				throw new CommandException("HELLO must be first command.");
			}

			arg = arg.toUpperCase().trim();//names are always capital
			this.game.clientHello(this, id, arg);
			outputMessage("HELLO " + arg);
			this.inGame = true;

		} else if (command.startsWith("LOOK")) {
			if (arg != null) {
				throw new CommandException("LOOK does not take an argument.");
			}

			outputMessage("LOOKREPLY" + System.getProperty("line.separator") + this.game.clientLook(id));

		} 
		
		else if (command.startsWith("SHOUT")) {
			// Ensure they have given us something to shout.
			if (arg.length() < 1) {
				throw new MessageException("need something to shout.");
			}

			this.game.clientShout(sanitiseMessage(arg), id);

		}
		
		else if (command.startsWith("WHISPER")) {
			if(arg == null) {
				throw new MessageException("need something to whisper.");
			}
			
			String[] arr = arg.split(" ", 2);
			
			if(arr.length < 2) {
				throw new MessageException("need something to whisper.");
			}
			
			this.game.clientWhisper(arr[1], this.id, arr[0]);
		}
		
		else if (command.startsWith("SETPLAYERPOS")) {
			if (arg == null) {
				throw new CommandException("need a position.");
			}

			// Obtain two co-ordinates
			final String coordinates[] = arg.split(" ");

			if (coordinates.length != 2) {
				throw new CommandException("need two co-ordinates.");
			}

			try {
				final int col = Integer.parseInt(coordinates[0]);
				final int row = Integer.parseInt(coordinates[1]);

				this.game.setPlayerPosition(id, col, row);
				outputSuccess();
				this.game.clientChange("");
			} catch (final NumberFormatException e) {
				throw new CommandException("co-ordinates must be integers.");
			}
		}
		
		//All above can be performed even if its not the players turn
		//Anything below cannot be
		else if (!usersTurn) {
			throw new CommandException("not users turn.");
		}
		
		else if (command.startsWith("PICKUP")) {
			if (arg != null) {
				throw new CommandException("PICKUP does not take an argument.");
			}

			this.game.clientPickup(id);
			outputSuccess();

		} else if (command.startsWith("MOVE")) {
			// We need to know which direction to move in.
			if (arg == null) {
				throw new CommandException("MOVE needs a direction.");
			}

			this.game.clientMove(getDirection(arg), id);
			outputSuccess();

		} else if (command.startsWith("ATTACK")) {
			// We need to know which direction to move in.
			if (arg == null) {
				throw new CommandException("ATTACK needs a direction.");
			}

			this.game.clientAttack(getDirection(arg), id);

			outputSuccess();

		} else if (command.startsWith("ENDTURN")) {
			this.game.clientEndTurn();

		} else {
			// If it is none of the above then it must be a bad command.
			throw new CommandException("invalid command.");
		}
	}

	/**
	 * Obtains a compass direction from a string. Used to ensure the correct exception type is
	 * thrown, and for consistency between MOVE and ATTACK.
	 * 
	 * @param string the direction string
	 * 
	 * @return the compass direction
	 * @throws CommandException
	 */
	protected CompassDirection getDirection(String string) throws CommandException {
		try {
			return CompassDirection.fromString(string);
		} catch (final IllegalArgumentException e) {
			throw new CommandException("invalid direction");
		}
	}

	/**
	 * Sanitises the given message - there are some characters that we can put in the messages that
	 * we don't want in other stuff that we sanitise.
	 * 
	 * @param s The message to be sanitised
	 * @return The sanitised message
	 */
	protected static String sanitiseMessage(String s) {
		return sanitise(s, "[a-zA-Z0-9-_ \\.,:;<!\\(\\)#]");
	}

	/**
	 * Strip out anything that isn't in the specified regex.
	 * 
	 * @param s The string to be sanitised
	 * @param regex The regex to use for sanitisiation
	 * @return The sanitised string
	 */
	protected static String sanitise(String s, String regex) {
		String rv = "";

		for (int i = 0; i < s.length(); i++) {
			final String tmp = s.substring(i, i + 1);

			if (tmp.matches(regex)) {
				rv += tmp;
			}
		}

		return rv;
	}

	/**
	 * Sends a success message in the event that a command has succeeded
	 */
	protected void outputSuccess() {
		outputMessage("SUCCESS");
	}

	/**
	 * Outputs a message to the player
	 * 
	 * @param message the message to send to the player.
	 */
	protected abstract void outputMessage(String message);

}
