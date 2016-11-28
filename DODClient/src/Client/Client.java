package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import GUI.GuiController;
/**
 * Extended by both HumanClient and BotClient, contains the common functionality
 * needed to send, receive and process data from the server. Also handles communication
 * with the GUI. 
 * @author charlie
 *
 */
public abstract class Client {
	protected Socket serverSocket = null;
	protected DataOutputStream outStream;
	protected BufferedReader inStream;
	protected GuiController gui;
	protected boolean inGame = false;//Primarily for directing fail messages
	protected char[][] currentLookReply;
	
	
	/**
	 * Constructor
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client(){
	}
	
	/**
	 * Attempts to start the connect between the client and the server.
	 * 
	 * @param ip
	 * @param port
	 * @param name requested name of the player
	 * @return success of connecting to server
	 */
	public void startGame(String ip, String port, String name){
		int portNum = 0;
		try{
			portNum = Integer.parseInt(port);
		}catch(NumberFormatException e)//not integer
		{
			this.gui.displayInvalidOptionPane("Invalid port number. Please try again.");
			return;
		}
		
		try {
			//avoids mutiple sockets being created
			if(serverSocket == null){
			this.serverSocket = new Socket(ip, portNum);
			this.outStream = new DataOutputStream(this.serverSocket.getOutputStream());
			this.inStream = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
			this.outputMessage("HELLO " + name);
			run();
			}
		} catch (IOException e) {
			this.gui.displayInvalidOptionPane("Invalid IP/port details. Please try again.");
			this.serverSocket = null;
			return;
		}
		
		
	}

	/**
	 * Will be the main loop of the client.
	 * creates a thread that will listen for data from the server.
	 * @throws IOException
	 */
	protected void run() throws IOException
	{
		//seperate thread for reading the inStream
				Thread inStreamThread = new Thread(new Runnable() {
					public void run() {
						while (true) {
							try {
								inputListen();
							} catch (UnknownHostException e) {
								e.printStackTrace();
								System.exit(1);
							} catch (SocketException e) {
								gui.displayInvalidOptionPane("The Server has Disconnected.");
								System.exit(1);
							} catch (NullPointerException e) {
								gui.displayInvalidOptionPane("The Server has Disconnected.");
								System.exit(1);
							} catch (IOException e) {
								e.printStackTrace();
								System.exit(1);
							}
						}
					}
				});
				inStreamThread.start();
	}
	
	
	/**
	 * Handles the LOOKREPLY and RENDERHINTS.
	 * 
	 * @throws IOException
	 */
	protected void handleLookReply() throws IOException {
		// Work out what the bot can see
		String line = receiveLine();
		final int lookReplySize = line.length();
		ArrayList<String> lines = new ArrayList<String>();
		this.currentLookReply = new char[lookReplySize][lookReplySize];
		
		//receive all layers of the loop reply
		lines.add(line);
		for (int i = 1; i < lookReplySize; i++) {
			lines.add(receiveLine());
		}
		//Processing the LOOKREPLY
		for (int row = 0; row < lookReplySize; row++) {
			for (int col = 0; col < lookReplySize; col++) {
				char c = lines.get(row).charAt(col);
				this.currentLookReply[row][col] = c;
			}
		}
		
		//receive render hints.
		line = receiveLine();
		while(!line.equals("RENDERHINT END") && !line.isEmpty()){
			lines.add(line);
			line = receiveLine();
		}
		//covert to array
		String[] lookLines = new String[lines.size()];
		lookLines = lines.toArray(lookLines);
		this.gui.updateDisplay(lookLines);
	}
	
	
	/**
	 * outputs a message to the server.
	 * @param string
	 * @throws IOException
	 */
	public void outputMessage(String string) throws IOException {
		outStream.writeBytes(string + System.getProperty("line.separator"));
		outStream.flush();
	}
	
	/**
	 * parses the message received from the server and takes an appropriate action.
	 * @param message message from the server.
	 * @throws IOException
	 */
	protected void parseMessage(String message) throws IOException{
		//game update
		if(message.startsWith("CHANGE") || message.startsWith("LOOKREPLY")){
			this.handleLookReply();
		}
		
		else{
			gui.update(message);
		}
	}
	
	/**
	 * Separate function to call receiveLine to allow thread be create universal function for inputStreamThread 
	 * @throws IOException
	 */
	protected void inputListen() throws IOException{
		String message = receiveLine();
		if(message != "")
		{
			parseMessage(message);
		}
	}

	/**
	 * @return last string received from the server
	 * @throws IOException
	 */
	protected String receiveLine() throws IOException {
		return inStream.readLine();
	}
}
