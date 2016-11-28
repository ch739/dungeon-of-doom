package Client;
import java.io.IOException;
import java.net.*;
import GUI.PlayerGUI;
/**
 * @author charlie
 *
 */
public class HumanClient extends Client {
	
	/**
	 * Constructor
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public HumanClient(){
		super();
		gui = new PlayerGUI(this);
	}

	@Override
	protected void run() throws IOException {
		super.run();
	}

}
