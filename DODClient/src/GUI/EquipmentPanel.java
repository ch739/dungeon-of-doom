package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * Equipment panel displays the current equipment that the player has acquired
 * and gold total.
 * 
 * @author charlie
 *
 */
public class EquipmentPanel extends JPanel{
	private static final long serialVersionUID = 260662120060327770L;
	
	private final static String SWORD_PATH = "res" + File.separator + "item" + File.separator + "sword.png";
	private final static String SWORD_BLACK_PATH = "res" + File.separator + "misc" + File.separator + "sword_black.png";
	private final static String ARMOUR_PATH = "res" + File.separator + "item" + File.separator + "armour.png";
	private final static String ARMOUR_BLACK_PATH = "res" + File.separator + "misc" + File.separator + "armour_black.png";
	private final static String LANTERN_PATH = "res" + File.separator + "item" + File.separator + "lantern.png";
	private final static String LANTERN_BLACK_PATH = "res" + File.separator + "misc" + File.separator + "lantern_black.png";
	private final static String GOLD_PATH = "res" + File.separator + "item" + File.separator + "gold.png";
	
	JLabel gold;
	JLabel sword;
	JLabel armour;
	JLabel lantern;
	int goldTot = 0;
	
	
	public EquipmentPanel(GuiController gui, boolean human)
	{
		this.setPreferredSize(new Dimension(180, 100));
		this.setBackground(Color.lightGray);
		this.setBorder(BorderFactory.createCompoundBorder(
		BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		
		gold = new JLabel();
		gold.setIcon(new ImageIcon(GOLD_PATH));
		gold.setBackground(null);
		gold.setBorder(null);
		this.increaseGold(goldTot);
		this.add(gold);
		
		sword = new JLabel();
		sword.setIcon(new ImageIcon(SWORD_BLACK_PATH));
		sword.setBackground(null);
		sword.setBorder(null);
		this.add(sword);
		
		armour = new JLabel();
		armour.setIcon(new ImageIcon(ARMOUR_BLACK_PATH));
		armour.setBackground(null);
		armour.setBorder(null);
		this.add(armour);
		
		lantern = new JLabel();
		lantern.setIcon(new ImageIcon(LANTERN_BLACK_PATH));
		lantern.setBackground(null);
		lantern.setBorder(null);
		this.add(lantern);
	}
	
	/**
	 * Increases the current gold displayed by n amount.
	 * @param n amount of gold to increase by.
	 */
	public void increaseGold(int n){
		goldTot += n;
		gold.setText("x" + goldTot);
	}

	/**
	 * updates the display in the panel.
	 * @param text equipment update type
	 * @throws NumberFormatException
	 */
	public void updateEquipment(String text) throws NumberFormatException {
		if(text.startsWith("TREASUREMOD")){
			text = text.replace("TREASUREMOD", "").trim();
			increaseGold(Integer.parseInt(text));//add on gold
		} 
		//item picked up
		else if(text.startsWith("EQUIP")){
			 text = text.replace("EQUIP", "").trim();
			 //replace image
			if(text.startsWith("SWORD")){
				sword.removeAll();
				sword.setIcon(new ImageIcon(SWORD_PATH));
			}  else if(text.startsWith("ARMOUR")){
				armour.removeAll();
				armour.setIcon(new ImageIcon(ARMOUR_PATH));
			} else if(text.startsWith("LANTERN")){
				lantern.removeAll();
				lantern.setIcon(new ImageIcon(LANTERN_PATH));
			}
		 }
		
		//refresh screen;
		this.revalidate();
		this.repaint();
		
	}

}
