package dod;

import java.io.IOException;
import java.net.*;
import dod.game.GameLogic;

/**
 * Handles clients attempting to create a pipe to the server.
 * Creates a new client thread for the client to communicate to the
 * server with.
 * Contains the server socket which accepts the clients.
 * @author charlie
 *
 */
public class Server {
	private GameLogic game = null;
	private ServerSocket ss;
	private final int PORT = 50000;
	

	/**
	 * @param map
	 * @throws IOException
	 */
	public Server(String map) throws IOException {
		try {

			game = new GameLogic(map);
			ss = new ServerSocket(PORT);
			ss.setSoTimeout(1000);
			this.listen();
		} catch (UnknownHostException e) {

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * adds a internal bot to the game in a thread.
	 * a feature will be added to add bots to game in CW3
	 */
	public void addBot() {
		Bot bot = new Bot(game);
		Thread thread = new Thread(bot);
		thread.start();
	}

	/**
	 * creates a new ClientThread will communication with the client.
	 * this will be contained in a thread.
	 * @param sock socket of the client
	 */
	private void addClient(Socket sock) {
		ClientThread ct = new ClientThread(sock, game);
		Thread thread = new Thread(ct);
		thread.start();
	}

	/**
	 * @return port number
	 */
	public int getPort() {
		return PORT;
	}

	/**
	 * listens for a client socket to try to connect and accepts and adding the
	 * client into the game.
	 */
	private void listen() {

		while (true) {
			try {
				final Socket sock = ss.accept();
				addClient(sock);
			} catch (Exception e) {
			}
		}

	}
}
