package dod;

/**
 * Class to handle command line arguments and initialise the correct instances.
 */
public class Program {

	/**
	 * Main method, used to parse the command line arguments.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {

		try {

			switch (args.length) {
				case 0:
					// No Command line arguments - default map
					System.out.println("Starting Game with Default Map");

					new Server("defaultMap");
					break;

				case 1:
					System.out.println("Starting Game with Map " + args[0]);
					new Server(args[0]);
					break;

				default:
					System.err.println("The wrong number of arguments have been provided");
					break;
			}
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}