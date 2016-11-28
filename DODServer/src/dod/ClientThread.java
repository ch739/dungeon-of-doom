package dod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import dod.game.GameLogic;

/**
 * Extends User class, handles the sending, retrieving and sending of data between the
 * client and the game.
 * 
 * @author charlie
 *
 */
public class ClientThread extends User {
	protected Socket clientSocket;
	protected DataOutputStream outStream;
	protected BufferedReader inStream;

	public ClientThread(Socket clientSocket, GameLogic game) {
		super(game);

		try {
			this.clientSocket = clientSocket;
			outStream = new DataOutputStream(clientSocket.getOutputStream());
			inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			clientClose();
		}
		
	}


	/**
	 * Listen for client commands and hands them over to be processed.
	 */
	@Override
	public void run() {

		try {
			while (true) {// Keep listening forever

				// Try to grab a command from the command line
				final String command = inStream.readLine();

				// Test for EOF (ctrl-D)
				if (command == null) {
					clientClose();
				}
				processCommand(command);
			}
		} catch (IOException e) {//checks if client disconnects
			clientClose();
		} catch (NullPointerException e){
			clientClose();
		}
	}

	/**
	 * Closes all the communication streams and the socket to prevent errors when the player quits.
	 * Also removes the player from game if they have entered the game.
	 */
	private void clientClose() {
		try {
			if(inGame){
				this.game.removePlayer(id);
				inGame = false;
			}
			
			clientSocket.close();
			inStream.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void outputMessage(String message) {
		try {
			outStream.writeBytes(message + System.getProperty("line.separator"));
			outStream.flush();//sends the string;
		} catch (IOException e) {
			this.game.removePlayer(id);
			clientClose();
		}
	}

}
