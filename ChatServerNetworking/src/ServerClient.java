import java.net.Socket;
import java.util.Date;

/**
 * Extension of the Client class to serve the server's needs. More client
 * information recording put into place along with a record of the Network Handler
 * and heartbeat update capabilities
 * @author ehaydenr
 *
 */
public class ServerClient extends Client {
	
	private Date requested, accepted, lastHeartBeat;
	private NetworkHandler handler;

	public ServerClient(Socket connection, NetworkHandler handler) {
		super(connection);
		this.handler = handler;
		this.lastHeartBeat = new Date();
		this.requested = new Date();
	}
	
	public void updateHeartBeat() {
		this.lastHeartBeat = new Date();
	}
	
	public void closeClient(){
		handler.removeClient(this);
		super.closeClient();
	}
	
	//		GETTERS AND SETTERS
	
	public Date getLastHeartbeat(){
		return this.lastHeartBeat;
	}
	
	public Date getRequested() {
		return requested;
	}

	public Date getAccepted() {
		return accepted;
	}
	
	public void setAccepted(Date accepted) {
		this.accepted = accepted;
	}
	


}
