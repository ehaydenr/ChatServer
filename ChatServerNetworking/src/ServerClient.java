import java.net.Socket;
import java.util.Date;


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
	
	public void closeClient(){
		handler.removeClient(this);
		super.closeClient();
	}

}
