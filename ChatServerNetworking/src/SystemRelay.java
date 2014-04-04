import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * At the start of the dueTime, the system will send messages to the client to check
 * to make sure the connection is maintained. If there is no response of the server
 * can't understand the response by the time the deadTime is reached, the client is closed.
 * 
 * @author ehaydenr
 * 
 */
public class SystemRelay extends Relay {

	private Message heartbeat;
	private Queue<ServerClient> clientsToBeClosed;
	private final long dueTime = 20000L; // 10 seconds - Starts prompting for heartbeat
	private final long deadTime = 30000L; // 20 seconds - Closes client

	public SystemRelay(NetworkHandler server) {
		super(server);
		this.heartbeat = new Message(MessageType.SYSTEM, new Integer(-1), null,
				"Server", null, "Verify Heartbeat");
		this.clientsToBeClosed = new LinkedList<ServerClient>();
	}

	public void processIncomingSystemMessage(Message message) {
		if (message.getContent().equals("Heatbeat Verified")) {
			server.updateHeartbeat(message.getRecipientId());
		}
	}

	@Override
	public void priorToSendingOutgoing() {
		synchronized (clientMap) {
			// Hearbeat
			for (Integer clientId : clientMap.keySet()) {
				ServerClient client = clientMap.get(clientId);
				Date current = new Date();
				long timeSinceLastHeartbeat = current.getTime()
						- client.getLastHeartbeat().getTime();
				if (timeSinceLastHeartbeat > this.dueTime) {
					if (timeSinceLastHeartbeat > this.deadTime) {
						this.clientsToBeClosed.add(client);
					} else {
						if (timeSinceLastHeartbeat % 1000 == 0) {
							// Assemble message
							Message.assembleClientMessage(this.heartbeat,
									client.getClientId(),
									client.getUsername());
							this.post(this.heartbeat);	// Will be processed in outgoing queue
						}
					}
				}
			}
		}

		// Close dead clients
		while (!this.clientsToBeClosed.isEmpty()) {
			ServerClient client = clientsToBeClosed.remove();
			Printer.println(this.getClass(), true, "Client dead: " + client.getUsername());
			client.closeClient();
		}
	}
}
