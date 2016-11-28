package GUI;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Abstract read/write panel. creates a panel with an area to input a string and a area to
 * display text received.
 * @author charlie
 *
 */
public abstract class RWPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = -992750085784341481L;
	protected JTextField textField;
	protected JTextPane textArea;
	protected GuiController parent;
	protected StyledDocument doc;//area text will be wrote to.
	protected StyleContext context;
	
	
	
	public RWPanel(GuiController gui, boolean human)
	{
		this.parent = gui;
		this.setPreferredSize(new Dimension(450, 200));
		this.setLayout(new BorderLayout());//fills whole Space
		if(human){
			//write area
			textField = new JTextField(47);
			textField.addActionListener(this);
			this.add(textField, BorderLayout.NORTH);
		}
		
		//read area
        textArea = new JTextPane();
        textArea.setPreferredSize(new Dimension(500,150));
        textArea.setBackground(Color.black);
        textArea.setForeground(Color.white);
        textArea.setEditable(false);
        textArea.setAutoscrolls(true);
        JScrollPane scrollBar = new JScrollPane(textArea);
        scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollBar, BorderLayout.CENTER);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);//stick to bottom of textArea.
        
        //styling the textPane
        doc = textArea.getStyledDocument();
        context = new StyleContext();
	}
	
	public abstract void actionPerformed(ActionEvent evt);
	
	public abstract void write(String text);

}
