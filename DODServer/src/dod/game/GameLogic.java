package dod.game;

import java.io.FileNotFoundException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import dod.game.items.GameItem;

/**
 * This class controls the game logic and other such magic.
 */
public class GameLogic {
	private Map map;
	private boolean playerWon = false;// Has a player won already?
	private ArrayList<Player> players;//list of all current players
	private Player activePlayer = null;//currently active player
	private final int MAX_NAME_LENGTH = 8;

	/**
	 * Constructor that specifies the map which the game should be played on.
	 * 
	 * @param mapFile The name of the file to load the map from.
	 * @throws FileNotFoundException , ParseException
	 */
	public GameLogic(String mapFile) throws FileNotFoundException, ParseException {
		this.map = new Map(mapFile);
		this.players = new ArrayList<Player>();
	}

	/**
	 * Adds a new player to the game. For now, only one player can be added.
	 * 
	 * @param listener the PlayerListener which will listen on behalf of that player, so messages
	 *            can be sent to the player
	 * @throws CommandException 
	 */
	public void addPlayer(PlayerListener listener, int id, String name) throws CommandException {
		synchronized (this.players) {
			for(Player p: players){
					if(p.getId() == id){
						throw new CommandException("Player already exsists.");
					}
			}
			Player player = new Player(name, generateRandomStartLocation(), listener, id);
			this.players.add(player);
			this.clientMessage(player.getName() + " has entered the game.");
			player.sendMessage("Welcome to Dungeon of Doom!" + System.getProperty("line.separator"));
			player.setGoal(this.getGoal());
			if (this.players.size() == 1) {
				startNewGame();
			}
		}
		
		this.clientChange("");//update clients
	}

	/**
	 * removes a player from the game, firstly dropping all the gold they have and
	 * then removing from the players array
	 * @param id player's id
	 */
	public void removePlayer(int id) {
		synchronized (this.players) {
			Player player = this.getPlayer(id);
			// activePlayer leaving during their turn;
			this.dropPlayerGold(player.getId());
			this.players.remove(player);
			this.clientMessage(player.getName() + " has left the game.");
			if (!this.playerWon && player == activePlayer && !this.players.isEmpty()) {
				//shift player turn
				this.clientStartTurn();
			}
		}
		
		this.clientChange("");//update clients
	}
	
	/**
	 * Generates a randomised start location
	 * 
	 * @return a random location where a player can start
	 * @throws CommandException 
	 */
	private Location generateRandomStartLocation() throws CommandException {
		if (!atLeastOneNonWallLocation()) {
			throw new CommandException("Map is currently full. Please try again later.");
		}
		synchronized (this.map) {
			while (true) {
				// Generate a random location
				final Random random = new Random();
				final int randomRow = random.nextInt(this.map.getMapHeight());
				final int randomCol = random.nextInt(this.map.getMapWidth());

				final Location location = new Location(randomCol, randomRow);
				if (this.map.getMapCell(location).isWalkable() && !this.tileHasPlayer(location)) {
					// If it's not a wall then we can put them there
					return location;
				}
			}
		}
	}

	/**
	 * Searches a possible tile to use by the player, i.e. non-wall. The map is traversed from (0,0)
	 * to (maxY,MaxX).
	 * 
	 * @return true if there is at least one non-wall location, false otherwise
	 */
	private boolean atLeastOneNonWallLocation() {
		synchronized (this.map) {
			for (int x = 0; x < this.map.getMapWidth(); x++) {
				for (int y = 0; y < this.map.getMapHeight(); y++) {
					Location loc = new Location(x,y);
					if (this.map.getMapCell(loc).isWalkable() && !this.tileHasPlayer(loc)) {
						// If it's not a wall/player then we can put them there
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * drops the gold from the player on the tile it stands on if there is a items already there,
	 * they are destroyed or if gold is there it is added together.
	 * 
	 * @param id id of the requested player
	 */
	private void dropPlayerGold(int id) {
		Player player = this.getPlayer(id);
		int gold = player.getGold();
		Tile currentTile = map.getMapCell(player.getLocation());

		if (currentTile.isWalkable() && gold > 0) {
			if (currentTile.hasItem() && currentTile.getItem().toChar() == 'G') {
				gold += currentTile.getItem().getAmount();
			}

			currentTile.addItem('G');
			currentTile.getItem().setAmount(gold);
		}
	}

	/**
	 * Starts a new game of the Dungeon of Dooooooooooooom.
	 * Reloads the map.
	 */
	private void startNewGame() {
		try {
			this.map.reload();
			this.playerWon = false;
			activePlayer = this.players.get(0);
			clientStartTurn();
			System.out.println("Starting new game.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e){
			e.printStackTrace();
		}

	}
	
	/**
	 * checks whether a chosen name by a client is a valid choice,
	 * based on its characters and if it is already in use.
	 * 
	 * @param name the name to be checked
	 * @throws CommandException
	 */
	private void checkValidName(String name) throws CommandException
	{
		if(name.length() == 0){//nothing entered.
			throw new CommandException("Please Enter your Characters Name.");
		}else if(name.length() > MAX_NAME_LENGTH){//too long
			throw new CommandException("Name too long (8 Characters).");
		}else if(name.startsWith("WHISPER")||name.startsWith("SENT")
				||name.startsWith("SHOUT")||name.startsWith("MESSAGE")){
			throw new CommandException("Invalid Name.");
		}
		
		for(int i = 0; i < name.length(); i++){
			String c = String.valueOf(name.charAt(i));
			if(!c.matches("[a-zA-Z0-9]+")){
				throw new CommandException("Invalid Characters in Name.");
			}
		}
		for(Player p: players){
			if(p.getName().equals(name)){
				throw new CommandException("Name is already in use.");
			}
		}
	}

	/**
	 * gets the next player in the array based on the currentplayers index number
	 * @param currentPlayer the active player
	 * @return the next player
	 */
	public Player getNextPlayer(Player currentPlayer) {
		if (this.players.isEmpty()) {
			throw new IllegalStateException("FAIL: There is no player on the map");
		}
		// make sure players array doesn't grow or shrink
		synchronized (this.players) {
			int index = this.players.indexOf(currentPlayer);
			index++;
			if (index >= this.players.size()) {//start back at the beginning
				index = 0;
			}
			return this.players.get(index);
		}
	}

	/**
	 * updates all clients of a update within the game
	 * @param update addition string information
	 */
	public void clientChange(String update) {
		for (Player p : this.players) {
			p.gameChange(update + System.getProperty("line.separator") + this.clientLook(p.getId()));
		}
	}

	/**
	 * Handles the client message HELLO
	 * 
	 * This is the command that enters a player into the game
	 * 
	 * @param name the name of the player to say hello to
	 * @param id id of the player trying to join the game
	 * @return the message to be passed back to the command line
	 * @throws CommandException
	 */
	public void clientHello(PlayerListener listener, int id, String name) throws CommandException {
		checkValidName(name);
		this.addPlayer(listener, id, name);
	}

	/**
	 * Handles the client message LOOK Shows the portion of the map that the player can currently
	 * see.
	 * 
	 * @param id id of the player calling the look command
	 * @return the part of the map that the player can currently see.
	 */
	public String clientLook(int id) {
		assertPlayerExists(id);
		Player player = getPlayer(id);
		// Work out how far the player can see
		final int distance = player.lookDistance();
		String lookReply = "";
		String renderHints = "";
		// synchronized map and players to create a "snap shot" of current view
		synchronized (this.map) {
			synchronized (this.players) {
				// Iterate through the rows.
				for (int rowOffset = -distance; rowOffset <= distance; ++rowOffset) {
					String line = "";

					// Iterate through the columns.
					for (int colOffset = -distance; colOffset <= distance; ++colOffset) {

						// Work out the location
						final Location location = player.getLocation().atOffset(colOffset, rowOffset);

						char content = '?';
						if (!player.canSeeTile(rowOffset, colOffset)) {
							// It's outside the FoV so we don't know what it is.
							content = 'X';
						} else if (!this.map.insideMap(location)) {
							// It's outside the map, so just call it a wall.
							content = '#';
						} else if (this.tileHasPlayer(location)) {
							
							if(rowOffset != 0 || colOffset != 0){//not equal to center, aka the calling player
								content = 'P';
							}else{
								content = this.map.getMapCell(location).toChar();//level below player
							}
							renderHints = renderHints + getRenderHint('P', location, (colOffset+distance), (rowOffset+distance));
						}
						// Look up and see what's on the map
						else {
							content = this.map.getMapCell(location).toChar();
							renderHints = renderHints + getRenderHint(content, location, (colOffset+distance), (rowOffset+distance));
						}

						// Add to the line
						line += content;
					}

					// Send a line of the look message
					lookReply += line + System.getProperty("line.separator");
				}
			}
		}
		renderHints = renderHints + "RENDERHINT END";
		return lookReply + renderHints;
	}
	
	/**
	 * Produces a render hint string for a particular tile. 
	 * Proving information about the player, item below and the tile at the bottom.
	 * Used by client if they wish to produce a detailed render.
	 * 
	 * @param c content on the tile at location 
	 * @param location position of the tile
	 * @param relativeX x relative to player
	 * @param relativeY y relative to player
	 * @return render hint
	 */
	private String getRenderHint(char c, Location location, int relativeX, int relativeY)
	{
		Tile tile = this.map.getMapCell(location);
		String renderHint = "RENDERHINT " + relativeX + " " + relativeY + " ";
		if(c == 'P'){
			Player p = getPlayerOnTile(location);
			renderHint = renderHint + "PLAYERTYPE " + p.getPlayerType() + " " + p.getLastDirection() + " " + p.getName() + " ";
			if(tile.hasItem()){
				renderHint = renderHint + "ITEMTYPE " + tile.getItem().toChar() + " ";
			}
		}
		renderHint = renderHint + "TILETYPE " + tile.getTileType() + System.getProperty("line.separator");
		return renderHint;
	}
	
	/**
	 * gets the player on a tile at a specific location.
	 * 
	 * @param location
	 * @return
	 */
	private Player getPlayerOnTile(Location location){
		for (Player p : this.players) {
			if (location.getRow() == p.getLocation().getRow() && location.getCol() == p.getLocation().getCol()) {
				return p;
			}
		}
		return null;
	}

	/**
	 * checks whether there is a player on a tile at a specific location.
	 * @param location
	 * @return
	 */
	private boolean tileHasPlayer(Location location) {
		for (Player p : this.players) {
			if (location.getRow() == p.getLocation().getRow() && location.getCol() == p.getLocation().getCol()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles the client message MOVE
	 * 
	 * Move the player in the specified direction - assuming there isn't a wall in the way
	 * 
	 * @param direction The direction (NESW) to move the player
	 * @return An indicator of the success or failure of the movement.
	 * @throws CommandException
	 */
	public void clientMove(CompassDirection direction, int id) throws CommandException {
		assertPlayerExists(id);
		ensureNoWinner();
		assertPlayerAP();

		// Work out where the move would take the player
		final Location location = activePlayer.getLocation().atCompassDirection(direction);

		this.activePlayer.setLastDirection(direction.toString());
		// Ensure that the movement is within the bounds of the map and not
		// into a wall
		synchronized (this.map) {// player made be added/removed while checking
			try{
			this.setPlayerPosition(id, location.getCol(), location.getRow());
			}catch(CommandException e){
				this.clientChange("");
				throw new CommandException(e.getMessage());
			}
			
			this.activePlayer.decrementAp();
			this.clientChange("");
			advanceTurn();
			return;
		}
	}

	/**
	 * Handles the client message ATTACK
	 * 
	 * Not currently implemented.
	 * 
	 * @param direction The direction in which to attack
	 * @return A message indicating the success or failure of the attack
	 * @throws CommandException
	 */
	public void clientAttack(CompassDirection direction, int id) throws CommandException {
		assertPlayerExists(activePlayer.getId());
		ensureNoWinner();
		assertPlayerAP();

		throw new CommandException("attacking (" + direction.toString() + ") has not been implemented.");
	}

	/**
	 * Handles the client message PICKUP. Generally it decrements AP, and gives the player the item
	 * that they picked up Also removes the item from the map
	 * 
	 * @return A message indicating the success or failure of the action of picking up.
	 * @throws CommandException
	 */
	public void clientPickup(int id) throws CommandException {
		assertPlayerExists(activePlayer.getId());
		ensureNoWinner();
		assertPlayerAP();

		final Tile playersTile = this.map.getMapCell(this.activePlayer.getLocation());
		synchronized (this.map) {
			// Check that there is something to pick up
			if (!playersTile.hasItem()) {
				throw new CommandException("nothing to pick up.");
			}

			// Get the item
			final GameItem item = playersTile.getItem();

			if (this.activePlayer.hasItem(item)) {
				throw new CommandException("already have item.");
			}

			this.activePlayer.giveItem(item);
			playersTile.removeItem();
			this.activePlayer.decrementAp();
			this.clientChange("");
			this.advanceTurn();
		}
	}

	/**
	 * Handles the client message SHOUT
	 * 
	 * Returns the current message to the client
	 * 
	 * @param message The message to be shouted
	 */
	public void clientShout(String message, int id) {
		String name = getPlayer(id).getName();
		for (Player p : this.players) {
			p.sendMessage("SHOUT " + name + ": " + message);
		}
	}
	
	/**
	 * Returns the a message to all clients when the server is
	 * communicating with the players.
	 * 
	 * @param message The message to be shouted
	 */
	public void clientMessage(String message) {
		for (Player p : this.players) {
			p.sendMessage("MESSAGE " + message);
		}
	}
	
	/**
	 * Handles the client message WHISPER
	 * 
	 * sends a private message to a specific client.
	 * @param message the message to be sent
	 * @param id id of the sender
	 * @param targetName name of the client to receive the message
	 * @throws MessageException
	 */
	public void clientWhisper(String message, int id, String targetName) throws MessageException{
		targetName = targetName.toUpperCase();
		String callerName = getPlayer(id).getName();
		if(callerName.equals(targetName)){
			throw new MessageException("cannot whisper to yourself.");
		}
		for (Player p : this.players) {
			if(p.getName().equals(targetName)){
				
				p.sendMessage("WHISPER " + callerName + ": " + message);
				getPlayer(id).sendMessage("SENT " + targetName + ": " + message);
				return;
			}
		}
		throw new MessageException("'"+targetName+"'" + " player does not exsist.");
	}

	/**
	 * Sets the player's position. It is particularly
	 * useful for testing, as it gets rounds the randomness of the player start position.
	 * 
	 * @param col the column of the location to put the player
	 * @param row the row to location to put the player
	 * @throws CommandException
	 */
	public void setPlayerPosition(int id, int col, int row) throws CommandException {
		Player player = this.getPlayer(id);
		final Location location = new Location(col, row);
		synchronized (this.map) {
			if (!this.map.insideMap(location)||!this.map.getMapCell(location).isWalkable()) {
				throw new CommandException("can't walk into this tile.");
			}
			else if (this.tileHasPlayer(location)) {
				throw new CommandException("can't walk into another player.");
			}

			player.setLocation(location);
		}
	}

	/**
	 * Passes the goal back
	 * 
	 * @return the current goal
	 */
	public int getGoal() {
		return this.map.getGoal();
	}



	/**
	 * Ensures a player has been added to the map. Otherwise, an exception is raised. In a
	 * multiplayer scenario, this could ensure a player by given ID exists.
	 * 
	 * @throws RuntimeException
	 */
	private void assertPlayerExists(int ID) throws RuntimeException {
		for (Player p : this.players) {
			if (p.getId() == ID) {
				return;
			}
		}
		throw new IllegalStateException("Player has not been added.");
	}

	/**
	 * Ensures a player has enough AP, otherwise a runtime error is raised, since the turn should
	 * have been advanced.
	 * 
	 * @throws RuntimeException
	 */
	private void assertPlayerAP() throws RuntimeException {
		if (this.activePlayer.remainingAp() == 0) {
			throw new IllegalStateException("Player has 0 ap");
		}
	}

	/**
	 * Ensure that no player has won the game. Throws a CommandException if someone has one,
	 * preventing the command from executing
	 * 
	 * @throws CommandException
	 */
	private void ensureNoWinner() throws CommandException {
		if (this.playerWon) {
			throw new CommandException("the game is over");
		}
	}

	/**
	 * get a player from the players array based on its id
	 * @param id id of the requested player
	 * @return returns the player
	 */
	private Player getPlayer(int id) {
		assertPlayerExists(id);
		Player player = null;
		for (Player p : this.players) {
			if (p.getId() == id) {
				player = p;
			}
		}
		if (player == null) {
			throw new IllegalStateException("Player does not exsist");
		}

		return player;
	}

	/**
	 * gets the next player to start their turn.
	 */
	private void clientStartTurn() {
		this.activePlayer = getNextPlayer(activePlayer);
		this.activePlayer.startTurn();
	}
	
	/**
	 * Handles the client message ENDTURN
	 * 
	 * Just sets the AP to zero and advances as normal.
	 * 
	 * @return A message indicating the status of ending a turn (currently always successful).
	 */
	public void clientEndTurn() {
		assertPlayerExists(activePlayer.getId());
		this.activePlayer.endTurn();
		clientStartTurn();
	}

	/**
	 * Once a player has performed an action the game needs to move onto the next turn to do this
	 * the game needs to check for a win and then test to see if the current player has more AP
	 * left.
	 * 
	 */
	private void advanceTurn() {
		// Check if the player has won
		if ((this.activePlayer.getGold() >= this.map.getGoal())
				&& (this.map.getMapCell(this.activePlayer.getLocation()).isExit())) {

			// Player should not be able to move if they have won
			assert (!this.playerWon);
			this.playerWon = true;
			
			//cloned because local bots will leave immediately and cause concurrency issues.
			@SuppressWarnings("unchecked")
			ArrayList<Player> copy = (ArrayList<Player>) this.players.clone();
			for(Player p: copy){ 
			       if (p != activePlayer) {
						p.lose();
			        }
			    }
			this.activePlayer.win();

		}else if (this.activePlayer.remainingAp() == 0) {
				// Force the end of turn
				clientEndTurn();
			}
	}

}