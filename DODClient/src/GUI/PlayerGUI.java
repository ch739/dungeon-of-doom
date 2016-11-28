package GUI;

import Client.Client;

/**
 * Extends Gui. Creates a Human interface, meaning that a human has access to commands.
 * @author charlie
 *
 */
public class PlayerGUI extends GuiController {
	private static final long serialVersionUID = 834948356103740948L;

	public PlayerGUI(Client parent)
	{
		super(parent, true);
		this.setTitle("Dungeon of Doom - Player View");
	}

}