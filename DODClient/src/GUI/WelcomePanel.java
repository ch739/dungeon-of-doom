package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
/**
 * Contains all the elements of the opening screen for the game. Contains the ip address,
 * port number and name. 
 * @author charlie
 *
 */
public class WelcomePanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 3265204985904446021L;
	//image paths
	private static final String BACKGROUND_IMAGE = "res"+File.separator+"misc"+File.separator+"title_background.png";
	private static final String TITLE_IMAGE = "res"+File.separator+"misc"+File.separator+"title.png";
	
	private GuiController parent;
	private JTextField nameField;
	private JTextField ipField;
	private JTextField portField;
	
	public WelcomePanel(GuiController gui)
	{
		this.parent = gui;
		this.setBackground(Color.black);
		
		JLayeredPane lp = new JLayeredPane();//set background underneath
		this.add(lp);
		lp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		//empty label to creat tab effect
		JLabel empty = new JLabel("                           ");
		lp.add(empty,c);
		
		
		//title
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weighty = 0.1;
		c.anchor = 	GridBagConstraints.SOUTH;
		JLabel title = new JLabel();
		title.setIcon(new ImageIcon(TITLE_IMAGE));
		lp.add(title,c);
		
		
		//ip
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel label = new JLabel("IP:", SwingConstants.RIGHT);
		label.setAlignmentX(RIGHT_ALIGNMENT);
		label.setHorizontalAlignment(JLabel.RIGHT);
		label.setHorizontalTextPosition(JLabel.RIGHT);
		label.setForeground(Color.white);
		lp.add(label,c);
		
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = 	GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		ipField = new JTextField(28);
		ipField.setText("127.0.0.1");
		lp.add(ipField,c);
		
		//port
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Port:");
		
		label.setForeground(Color.white);
		lp.add(label,c);
		

		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = 	GridBagConstraints.WEST;
		portField = new JTextField(28);
		portField.setText("50000");
		lp.add(portField,c);
		
		//name
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		label = new JLabel("Name:");
		label.setForeground(Color.white);
		lp.add(label,c);
		
		
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 2;
		c.anchor = 	GridBagConstraints.WEST;
		nameField = new JTextField(28);
		lp.add(nameField,c);

		//start btn
		c.anchor = 	GridBagConstraints.CENTER;
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1;
		JButton startButton = new JButton("Enter Game");
		startButton.addActionListener(this);
		startButton.setActionCommand("START");
		startButton.setFocusPainted(false);
		startButton.setPreferredSize(new Dimension(120, 20));
		lp.add(startButton,c);
		
		//quit btn
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 3;
		c.gridy = 6;
		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(this);
		quitButton.setActionCommand("QUIT");
		quitButton.setFocusPainted(false);
		quitButton.setPreferredSize(new Dimension(120, 20));
		lp.add(quitButton,c);
		
		//background
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 5;
		c.gridheight = 8;
		JLabel backGround = new JLabel();
		backGround.setIcon(new ImageIcon(BACKGROUND_IMAGE));
		lp.add(backGround,c);
	
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("START")){
			startGame();
		}else{
		parent.outputMessage(cmd);//quit
		}
	}
	
	/**
	 * calls the parent Gui to process startGame. 
	 */
	private void startGame(){
		parent.startGame(ipField.getText(), portField.getText(), getName());
	}
	
	/**
	 * return the text in the nameField.
	 */
	public String getName()
	{
		return nameField.getText().toUpperCase().trim().replace(" ","");
	}
}
