package GUI;

import Client.Client;

/**
 * Extends Gui. Creates a Bot interface, meaning that a human has no access to commands.
 * @author charlie
 *
 */
public class BotGUI extends GuiController{
	private static final long serialVersionUID = 6415203816341649923L;

	public BotGUI(Client parent) {
		super(parent, false);
		this.setTitle("Dungeon of Doom - Bot View");
	}

}
