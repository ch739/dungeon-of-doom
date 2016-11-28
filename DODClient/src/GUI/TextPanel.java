package GUI;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

//http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabbedPaneDemoProject/src/components/TabbedPaneDemo.java
/**
 * Contains both the chatPanel and CommandPanel via a tab layout.
 * 
 * @author charlie
 */
public class TextPanel extends JPanel {
	private static final long serialVersionUID = -5746227332829013907L;
	ChatPanel chatPanel;
	CommandPanel commandPanel;
	GuiController parent;
	
	public TextPanel(GuiController gui, boolean human)
	{
		super(new GridLayout(1,1));
		this.parent = gui;
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		
		
		//chat open on bot for humans;
		chatPanel = new ChatPanel(gui, true);
		commandPanel = new CommandPanel(gui, human);
		 JTabbedPane tabbedPane = new JTabbedPane();
		 tabbedPane.addTab("Log", commandPanel);
		 tabbedPane.addTab("Chat", chatPanel);
		 this.add(tabbedPane);
		 
	}
	
	/**
	 * writes text to the commandPanel.
	 * @param text text to be write to the commandPanel
	 */
	public void updateLog(String text){
		commandPanel.write(text);
	}
	
	/**
	 * writes text to the chatPanel.
	 * @param text text to be write to the chatPanel
	 */
	public void updateChat(String text)
	{
		text = text.replaceFirst("MESSAGE ", "");
		chatPanel.write(text);
	}
}
