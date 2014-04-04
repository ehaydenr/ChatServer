import java.net.Socket;
import java.util.Date;

/**
 * Client to be used by server and client programs. This should provide the necessary functions
 * to interact with a client without being worried about networked streams and other things like that.
 * Client object has a unique ID along with a username field.
 * 
 * Close client should be called to terminate client and streams below it. When extending, override closeClient
 * to perform operations such as removing client from a list in the main program.
 * @author ehaydenr
 */

public class Client {
	private Integer clientId;
	private IOStreamsHandler io;
	private String username = "Unknown";
	
	private static Integer idCounter = 0;
	
	public Client(Socket connection){
		this.clientId = idCounter++;
		this.io = new IOStreamsHandler(this, connection);
		this.io.start();
	}
	
	/**
	 * @param message - Message to be sent
	 * @param urgent - If urgent, it will block and send directly
	 */
	public void write(Message message, boolean urgent){
		io.post(message, urgent);
	}

	/**
	 * @return message received or null if no messages
	 */
	public Message read(){
		return io.pull();
	}

	public void closeClient(){
		io.close();
	}
	
	// Getters and Setters
	
	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public static Integer getIdCounter() {
		return idCounter;
	}
	
}
