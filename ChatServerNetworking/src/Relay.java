import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Allow messages to be added and then sent to the intended clients
 * @author ehaydenr
 *
 */

public abstract class Relay extends Thread{
	protected NetworkHandler server;
	protected Queue<Message> outgoing;
	protected HashMap<Integer, ServerClient> clientMap;
	
	public Relay(NetworkHandler server){
		this.server = server;
		this.clientMap = server.getClientMap();
		this.outgoing = new LinkedList<Message>();
	}
	
	public void post(Message message){
		synchronized(outgoing){
			outgoing.add(message);
		}
	}
	
	public abstract void priorToSendingOutgoing();
	
	/**
	 * Print out all messages in the outgoing Queue to their respective clients
	 */
	public void run(){
		
		// Print out all outgoing
		while (!this.isInterrupted()) {
			
			// Run subclass specified code
			this.priorToSendingOutgoing();
			
			synchronized (this.outgoing) {
				while(!this.outgoing.isEmpty()) {
					Message message = this.outgoing.remove();
					Integer recipientId = message.getRecipientId();
					if (recipientId.equals(-1)) {
						// Send to all clients
						synchronized (clientMap) {
							for (Integer id : clientMap.keySet()) {
								ServerClient client = clientMap.get(id);
								client.write(message, false);
								Printer.println(
										this.getClass(),
										false,
										"Writing PUBLIC message to client: "
												+ message.toString());
							}
						}
					} else {
						// Send to select clients
						synchronized (clientMap) {
							clientMap.get(recipientId).write(message, false);
							Printer.println(
									this.getClass(),
									false,
									"Writing PRIVATE message to client: "
											+ message.toString());
						}
					}
				}
			}
		}
	}
}
