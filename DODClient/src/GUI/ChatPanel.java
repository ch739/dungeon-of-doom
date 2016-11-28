package GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;
/**
 * Extends RWPanel (read/write). Chat panel for the GUI. This is where the player will be able to 
 * send and receive messages from players also player the game.
 * @author charlie
 *
 */
public class ChatPanel extends RWPanel {
	private static final long serialVersionUID = -5620548958682329716L;
	
	private static final String PATH = "res"+File.separator+"smiley"+File.separator;
	//text styles
	private Style whisper;
	private Style sent;
	private Style fail;
	private Style standard;
	
	//emotion icons
	static final String SMILE_EMOTICON = ":)", SAD_EMOTICON = ":(", HOHO_EMOTICON = ":D", TONGUE1_EMOTICON = ":P",
			TONGUE2_EMOTICON = ":p", WINK_EMOTICON = ";)", HEART_EMOTICON = "<3";
	
	//emotion icons array;
	private String[] emoticons = {SMILE_EMOTICON, SAD_EMOTICON, HOHO_EMOTICON, TONGUE1_EMOTICON, TONGUE2_EMOTICON,
			WINK_EMOTICON, HEART_EMOTICON};
	
	public ChatPanel(GuiController gui, boolean human)
	{
		super(gui, human);
		
		whisper = context.addStyle("whisper", null);
        StyleConstants.setForeground(whisper, Color.MAGENTA);
        
        sent = context.addStyle("sent", null);
        StyleConstants.setForeground(sent, Color.gray);
        
        fail = context.addStyle("fail", null);
        StyleConstants.setForeground(fail, Color.yellow);
        
        standard = context.addStyle("standard", null);
        StyleConstants.setForeground(standard, Color.gray);
        StyleConstants.setBold(standard, true);
        

	}
	
	/**
	 * gets the emoticon imageIcon of an associated string.
	 * 
	 * @param type
	 * @return ImageIcon
	 */
	private ImageIcon getEmotIcon(String type){
		if(type.equals(SMILE_EMOTICON)){
			return new ImageIcon(PATH  + "smile.png");
		}else if(type.equals(SAD_EMOTICON)){
			return new ImageIcon(PATH  + "sad.png");
		}else if(type.equals(HOHO_EMOTICON)){
			return new ImageIcon(PATH  + "hoho.png");
		}else if(type.equals(TONGUE1_EMOTICON) || type.equals(TONGUE2_EMOTICON)){
			return new ImageIcon(PATH  + "tongue.png");
		}else if(type.equals(WINK_EMOTICON)){
			return new ImageIcon(PATH  + "wink.png");
		}else if(type.equals(HEART_EMOTICON)){
			return new ImageIcon(PATH  + "heart.png");
		}
		return null;
	}
	
	//http://stackoverflow.com/questions/13716769/java-string-replaceall
	/**
	 * Checks for any characters in a string that are associated to emoticons and replaces
	 * the characters with the appropriate imageIcon.
	 * @param string
	 * @throws BadLocationException
	 */
	private  void updateEmotIcons(String string) throws BadLocationException{
             int start = Utilities.getWordStart(textArea, doc.getEndPosition().getOffset() - string.length()-1);
             int end = Utilities.getWordStart(textArea, doc.getEndPosition().getOffset()-2);//taking into account /n
             int i = start;
             	while (i < end) {
                     final SimpleAttributeSet attrs = new SimpleAttributeSet(doc.getCharacterElement(i).getAttributes());
                    	String snip = doc.getText(i, 2);
                    	
                    	for(String emo: emoticons){
                    		if(snip.equals(emo)){
                    			StyleConstants.setIcon(attrs, getEmotIcon(snip));
                    			//replace
                            	doc.remove(i, 2);
                                doc.insertString(i, snip, attrs);
                    			break;
                    		}
                    	}
                    	
                    	 i++;
                     }
                    
	}
	
	/**
	 * writes a string to the end of the textArea and checks the type of the string to
	 * decide what style it should be
	 * @param s string
	 */
	public void write(String s)
	{
		try {
			if(s.startsWith("MESSAGEFAIL")){
				doc.insertString(doc.getLength(), s + "\n", fail);
			}else{
				if(s.startsWith("WHISPER")){
					s = s.replaceFirst("WHISPER", "").trim();
					doc.insertString(doc.getLength(), s + "\n", whisper);
				}else if(s.startsWith("SENT")){
					doc.insertString(doc.getLength(), s + "\n", sent);
				}else if(s.startsWith("SHOUT")){
					s = s.replaceFirst("SHOUT", "").trim();
					doc.insertString(doc.getLength(), s + "\n", null);
				}else{
					doc.insertString(doc.getLength(), s + "\n", standard);
				}
				//replace added string
				updateEmotIcons(s);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		//set the type of message being sent.
		String text = textField.getText().trim();
		if(text.toUpperCase().startsWith("WHISPER")){
			this.parent.outputMessage(text);
		}else{
			this.parent.outputMessage("SHOUT " + text);
		}
		textField.setText("");
	}
}
