package Client;

/**
 * Starts the client.
 * @author charlie
 *
 */
public class PlayGame {

	public static void main(String[] args) {
			switch (args.length) {
				
				case 0://human
					new HumanClient();
					break;
				case 1:
					if (args[0].equals("-b")) {//bot
						new BotClient();
						break;
					} else {
						System.err.println("The wrong arguments have been provided");
						System.exit(1);
					}
					break;
				default:
					System.err.println("The wrong number of arguments have been provided, you can specify -b to play with a bot");
					break;
			}
	}
}
