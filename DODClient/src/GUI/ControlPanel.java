package GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This contains the buttons that allows the human user to control their player.
 * They commands will not be visible for the bot.
 * @author charlie
 *
 */
public class ControlPanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 2324829572256079798L;
	private static final String QUIT_PATH = "res" + File.separator + "controls"+ File.separator + "quit.png";
	private static final String QUIT_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "quit_over.png";
	private static final String NORTH_PATH = "res" + File.separator + "controls"+ File.separator + "north_arrow.png";
	private static final String NORTH_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "north_arrow_over.png";
	private static final String EAST_PATH = "res" + File.separator + "controls"+ File.separator + "east_arrow.png";
	private static final String EAST_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "east_arrow_over.png";
	private static final String SOUTH_PATH = "res" + File.separator + "controls"+ File.separator + "south_arrow.png";
	private static final String SOUTH_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "south_arrow_over.png";
	private static final String WEST_PATH = "res" + File.separator + "controls"+ File.separator + "west_arrow.png";
	private static final String WEST_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "west_arrow_over.png";
	private static final String PICKUP_PATH = "res" + File.separator + "controls"+ File.separator + "pickup.png";
	private static final String PICKUP_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "pickup_over.png";
	private static final String ENDTURN_PATH = "res" + File.separator + "controls"+ File.separator + "endturn.png";
	private static final String ENDTURN_OVER_PATH = "res" + File.separator + "controls"+ File.separator + "endturn_over.png";
	
	
	
	GuiController parent;
	
	public ControlPanel(GuiController gui, boolean human)
	{
		this.parent = gui;
		this.setPreferredSize(new Dimension(180,300));
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.darkGray);
		this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		
		GridBagConstraints c = new GridBagConstraints();
		
		JButton btn;
		
		//quit btn
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHEAST;
		btn = new JButton();
		btn.addActionListener(this);
		btn.setIcon(new ImageIcon(QUIT_PATH));
		btn.setRolloverIcon(new ImageIcon(QUIT_OVER_PATH));
		btn.setToolTipText("Quit");
		//clear background
		btn.setBackground(null);
		btn.setFocusPainted(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setBorder(null);
		btn.setActionCommand("QUIT");
		this.add(btn,c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 2;
		c.weighty = 2;
		this.add(new JLabel(), c);//empty
		
		if(human){
			//north btn
			c.gridx = 2;
			c.gridy = 2;
			c.weighty = 0;
			c.weightx = 0;
			c.ipady = 20;
			btn = new JButton();
			btn.addActionListener(this);
			btn.setIcon(new ImageIcon(NORTH_PATH));
			btn.setRolloverIcon(new ImageIcon(NORTH_OVER_PATH));//highlight
			btn.setToolTipText("Move North");//scroll over prompt
			btn.setBackground(null);
			btn.setFocusPainted(false);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setBorder(null);
			btn.setActionCommand("MOVE N");
			this.add(btn,c);
			
			//east btn
			c.gridx = 3;
			c.gridy = 3;
			btn = new JButton();
			btn.addActionListener(this);
			btn.setIcon(new ImageIcon(EAST_PATH));
			btn.setRolloverIcon(new ImageIcon(EAST_OVER_PATH));
			btn.setToolTipText("Move East");
			btn.setBackground(null);
			btn.setFocusPainted(false);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setBorder(null);
			btn.setActionCommand("MOVE E");
			this.add(btn,c);
			
			//west btn
			c.gridx = 1;
			c.gridy = 3;
			btn = new JButton();
			btn.addActionListener(this);
			btn.setIcon(new ImageIcon(WEST_PATH));
			btn.setRolloverIcon(new ImageIcon(WEST_OVER_PATH));
			btn.setToolTipText("Move West");
			btn.setBackground(null);
			btn.setFocusPainted(false);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setBorder(null);
			btn.setActionCommand("MOVE W");
			this.add(btn,c);
			
			
			//south btn
			c.gridx = 2;
			c.gridy = 4;
			btn = new JButton();
			btn.addActionListener(this);
			btn.setIcon(new ImageIcon(SOUTH_PATH));
			btn.setRolloverIcon(new ImageIcon(SOUTH_OVER_PATH));
			btn.setToolTipText("Move South");
			btn.setBackground(null);
			btn.setFocusPainted(false);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setBorder(null);
			btn.setActionCommand("MOVE S");
			this.add(btn,c);
			
			//pickup btn
			c.gridx = 2;
			c.gridy = 3;
			btn = new JButton();
			btn.addActionListener(this);
			btn.setIcon(new ImageIcon(PICKUP_PATH));
			btn.setRolloverIcon(new ImageIcon(PICKUP_OVER_PATH));
			btn.setToolTipText("Pickup");
			btn.setBackground(null);
			btn.setFocusPainted(false);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setBorder(null);
			btn.setActionCommand("PICKUP");
			this.add(btn,c);
			
			//endturn btn
			c.weighty = 2;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 6;
			btn = new JButton();
			btn.addActionListener(this);
			btn.setIcon(new ImageIcon(ENDTURN_PATH));
			btn.setRolloverIcon(new ImageIcon(ENDTURN_OVER_PATH));
			btn.setBackground(null);
			btn.setFocusPainted(false);
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setBorder(null);
			btn.setActionCommand("ENDTURN");
			this.add(btn,c);
		}
		
	}


	public void actionPerformed(ActionEvent e) {
		parent.outputMessage(e.getActionCommand());//on button click
	}
}
