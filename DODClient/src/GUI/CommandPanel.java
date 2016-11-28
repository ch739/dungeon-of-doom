package GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * Extends the RWPanel (Read/write). Command panel for the GUI. This is where the player will be able to 
 * send and receive commands from the server. 
 * @author charlie
 *
 */
public class CommandPanel extends RWPanel{
	private static final long serialVersionUID = -4807638777312437850L;
	private Style fail;
	private Style important;
	private Style equip;

	public CommandPanel(GuiController gui, boolean human)
	{
		super(gui, human);
       fail = context.addStyle("fail", null);
       StyleConstants.setForeground(fail, Color.yellow);
       
       important = context.addStyle("important", null);
       StyleConstants.setForeground(important, Color.blue);
       StyleConstants.setBold(important, true);
       
       equip = context.addStyle("equip", null);
       StyleConstants.setForeground(equip, Color.orange);
       StyleConstants.setBold(equip, true);
	}
	
	/**
	 * writes a string to the end of the textArea and checks the type of the string to
	 * decide what style it should be
	 * @param s string
	 */
	public void write(String s)
	{
		try {
			if(s.startsWith("FAIL")){
			doc.insertString(doc.getLength(), s + "\n", fail);
			}else if(s.startsWith("STARTTURN")|| (s.startsWith("ENDTURN")||s.startsWith("GOAL")
					||s.startsWith("WIN") ||s.startsWith("LOSE"))){
				doc.insertString(doc.getLength(), s + "\n", important);
			}else if(s.startsWith("EQUIP")||s.startsWith("TREASUREMOD")||s.startsWith("HITMOD")){
				doc.insertString(doc.getLength(), s + "\n", equip);
			}else{
				doc.insertString(doc.getLength(), s + "\n", null);	
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		this.parent.outputMessage(textField.getText());
		textField.setText("");
	}
}
