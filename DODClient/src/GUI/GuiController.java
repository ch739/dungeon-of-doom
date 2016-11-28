package GUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Client.Client;

/**
 * Abstract class which contains all the panels in the gui and also communicates between
 * the panels and the client.
 * @author charlie
 *
 */
public abstract class GuiController extends JFrame implements ActionListener{
	private static final long serialVersionUID = 3665916598965157624L;
	private static boolean inGame;
	
	private WelcomePanel welcomePanel;
	private JPanel gamePanel;//contains all other panels.
	private DisplayGrid gameView;
	private ControlPanel controlPanel;
	private EquipmentPanel equipmentPanel;
	private TextPanel textPanel;
	private Client parent;
	private Container canvas;

	
	public GuiController(Client parent, boolean human)
	{
		this.parent = parent;
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);//force to use quit button.
		this.setPreferredSize(new Dimension(600, 680));
		this.setResizable(false);
		canvas = this.getContentPane();
		
		welcomePanel = new WelcomePanel(this);
		canvas.add(welcomePanel);//seperate screen
		
		gamePanel = new JPanel();
		gamePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//game view
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		this.gameView = new DisplayGrid();
		gamePanel.add(gameView,c);
		
		//control panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 3;
		this.controlPanel = new ControlPanel(this, human);
		gamePanel.add(controlPanel,c);
		
		//equipment panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		c.gridheight = 2;
		this.equipmentPanel = new EquipmentPanel(this, human);
		gamePanel.add(equipmentPanel,c);
		
		//text panel
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.gridheight = 1;
		this.textPanel = new TextPanel(this, human);
		gamePanel.add(textPanel,c);
		
		this.pack();
		this.setVisible(true);
		
		//center screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}
	
	/**
	 * Sends a request to the client to start the game with the provided parameters.
	 * @param ip address
	 * @param port
	 * @param name name of player
	 */
	public void startGame(String  ip, String port, String name)
	{
		this.parent.startGame(ip, port, name);
	}
	
	/**
	 * when the client has successfully connected to the server this is called and
	 * will display the game view to the user.
	 */
	public void startGameGui()
	{
		this.setVisible(false);
		canvas.removeAll();
		canvas.add(gamePanel);
		this.setVisible(true);
	}
	
	/**
	 * parses the message received from the client and takes an appropriate action.
	 * @param message message from the server.
	 * @throws IOException
	 */
	protected void parseMessage(String message){
		//start game!
		if(message.startsWith("HELLO")){
			inGame = true;
			startGameGui();
		}
		//fail
		if(message.startsWith("FAIL")){
			if(!inGame){
				message = message.replaceFirst("FAIL", "");
				displayInvalidOptionPane(message);
			}else{
				updateLog(message);
			}
		}
		//message
		else if(message.startsWith("MESSAGE") || message.startsWith("WHISPER") || message.startsWith("SENT")
				|| message.startsWith("SHOUT")){
			updateChat(message);
		}
		//equipment change
		else if(message.startsWith("EQUIP")|| message.startsWith("TREASUREMOD")||  message.startsWith("HITMOD")){
			updateEquipment(message);
			updateLog(message);
		}
		
		//win
		else if(message.startsWith("WIN")){
			displayGameOver(true);
			updateLog(message);
		}
		
		//lose
		else if(message.startsWith("LOSE")){
			displayGameOver(false);
			updateLog(message);
		}
		
		else{
			updateLog(message);
		}
	}
	
	public void update(String message){
		parseMessage(message);
	}
	
	
	/**
	 * updates the game view
	 * @param lines the current look reply and renderhints
	 * @throws IOException
	 */
	public void updateDisplay(String[] lines) throws IOException{
		this.gameView.handleLookReply(lines);
	}

	public void actionPerformed(ActionEvent evt) {
		
	}
	
	/**
	 * outputs a message to the client to send on to the server.
	 * @param string
	 */
	public void outputMessage(String string)
	{
		try {
			//quit game
			if(string.equals("QUIT"))
			{
				this.quit();
			} 
			else{
				parent.outputMessage(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * display that the game is over in the game view
	 * @param win whether the player won or lost
	 */
	protected void displayGameOver(boolean win){
		this.gameView.displayGameOver(win);
	}
	
	/**
	 * updates the game log
	 * @param text
	 */
	
	protected void updateLog(String text)
	{
		textPanel.updateLog(text);
	}
	
	/**
	 * updates the chat panel 
	 * @param text
	 */
	protected void updateChat(String text)
	{
		textPanel.updateChat(text);
	}
	
	/**
	 * update the equipment panel
	 * @param text
	 */
	protected void updateEquipment(String text){
		equipmentPanel.updateEquipment(text);
	}
	
	/**
	 * quits the game.
	 */
	protected void quit()
	{
		int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to Quit?","Choose", JOptionPane.YES_NO_OPTION);
		if(choice==JOptionPane.YES_OPTION){
		this.dispose();
		System.exit(0);
		}
	}
	
	/**
	 * shows pop up message
	 * 
	 * @param message
	 */
	public void displayInvalidOptionPane(String message)
	{
		JOptionPane.showMessageDialog(this, message);
	}

}
