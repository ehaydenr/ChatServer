import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

/**
 * pull all recieved messages from accepted clients and forward them to their
 * respective threads (System, chat, applied)
 * 
 * NetworkManager Object
 * 
 * Fields Client Mapping of clientIDs to client usernames //Concurrent Utils?
 * this is so clientId lookup is easier List of client objects // Concurrent
 * Utils? Request Handler
 * 
 * Methods addClient(Client) removeClient(Client) sendMessage(ClientId)
 * sendBlastMessage(String content)
 * {"type":"SYSTEM","senderId":-1,"recipientId":0,"content":"Eric","senderUsername":"Server","recipientUsername":"Unknown","timeSent":"Mar 21, 2014 3:40:26 PM"}
 * 
 * @author ehaydenr
 * 
 */
public class NetworkHandler extends Thread {

	private HashMap<Integer, ServerClient> clientMap;
	private RequestHandler requestHandler;
	private Interrogator interrogator;
	private Responder responder;
	private ChatRelay chatRelay;
	private SystemRelay systemRelay;
	private AppliedRelay appliedRelay;
	private final int DEFAULT_CLIENT_CAP = 30;
	private int capOfClientsConnected;
	private final static int DEFAULT_PORT = 7001;

	private String ipString;

	public NetworkHandler(Responder responder, Integer port, Integer clientCap) {
		this.clientMap = new HashMap<Integer, ServerClient>();
		this.responder = responder;
		this.requestHandler = new RequestHandler(this, port != null ? port : DEFAULT_PORT); //If port null will defer to default
		this.interrogator = new Interrogator(this);
		capOfClientsConnected = clientCap != null ? clientCap : DEFAULT_CLIENT_CAP;

		this.chatRelay = new ChatRelay(this);
		this.systemRelay = new SystemRelay(this);
		this.appliedRelay = new AppliedRelay(this);
		
		this.chatRelay.setName("Chat Relay");
		this.systemRelay.setName("System Relay");
		this.appliedRelay.setName("Applied Relay");
		this.interrogator.setName("Interrogator");
		this.requestHandler.setName("Request Handler");
		this.setName("NetworkHandler");
		
	}
	
	public void start(){
		this.chatRelay.start();
		this.systemRelay.start();
		this.appliedRelay.start();
		this.interrogator.start();
		this.requestHandler.start(); // Request handler last to avoid race
										// condition - no clients = no use of
										// previously started threads
		
		super.start();
	}

	public void run() {
		// Read from clients and distribute to Relay threads
		while (!this.isInterrupted()) {
			synchronized (clientMap) {
				for (Integer clientId : clientMap.keySet()) {
					ServerClient client = clientMap.get(clientId);
					Message input = client.read();
					if (input != null) {
						this.addMessage(input);
						client.updateHeartBeat();
					}
				}
			}
		}
	}

	public void addMessage(Message message) {
		if (message.getType() != null) {
			switch (message.getType()) {
			case CHAT:
				chatRelay.post(message);
				responder.chatMessageSent(message);
				break;
			case SYSTEM:
				if (message.getRecipientId().equals(-1)) {
					systemRelay.processIncomingSystemMessage(message);
				} else
					systemRelay.post(message);
				break;
			case APPLIED:
				appliedRelay.post(message);
				break;
			}
		}
	}

	public void setResponder(Responder responder) {
		this.responder = responder;
	}

	public void setIpString(String string) {
		this.ipString = string;
	}

	public String getIpString() {
		return this.ipString;
	}

	public void interrogate(Socket connection) {
		this.interrogator.addConnection(connection);
	}

	public void addClient(ServerClient client) {
		synchronized (clientMap) {
			clientMap.put(client.getClientId(), client);
		}
		Message welcome = responder.getWelcomeMessage(client);
		if (welcome != null) {
			client.write(welcome, false);
		}

		// Trigger Responder
		if (this.responder != null) {
			responder.clientRecieved(client);
		}
	}

	public void removeClient(ServerClient client) {
		synchronized (clientMap) {
			clientMap.remove(client.getClientId());
		}
		Message goodbye = responder.getGoodbyeMessage(client);
		if (goodbye != null)
			client.write(goodbye, true); // Should block till message is sent

		// Trigger Responder
		if (this.responder != null) {
			responder.clientDisconnected(client);
		}
	}

	public void appliedMessageRecieved(Message message) {
		this.responder.appliedMessageRecieved(message);
	}

	public boolean clientCapReached() {
		synchronized (this.clientMap) {
			boolean reached = this.clientMap.size() >= this.capOfClientsConnected;
			if (reached) {
				this.responder.capOfClientsReached();
			}
			return reached;
		}
	}

	public static void main(String[] args) {
		NetworkHandler handler = new NetworkHandler(new BasicResponder(), null, null);
		handler.start();
	}

	public void updateHeartbeat(Integer recipientId) {
		synchronized (this.clientMap) {
			clientMap.get(recipientId).updateHeartBeat();
		}
	}

	public HashMap<Integer, ServerClient> getClientMap() {
		return this.clientMap;
	}

	public void shutdown() {
		Printer.println(this.getClass(), false, "Shutdown called.. Interrupting threads");
		this.chatRelay.interrupt();
		this.systemRelay.interrupt();
		this.appliedRelay.interrupt();
		this.interrogator.interrupt();
		this.requestHandler.interrupt();
		this.interrupt();
		
		Set<Integer> clientIds;
		synchronized (clientMap) {
			clientIds = clientMap.keySet();
			for (Integer id : clientIds) {
				ServerClient client = clientMap.get(id);
				clientMap.remove(client);
				
				Message goodbye = responder.getGoodbyeMessage(client);
				if (goodbye != null)
					client.write(goodbye, true); // Should block till message is sent
				client.closeClient();

				// Trigger Responder
				if (this.responder != null) {
					responder.clientDisconnected(client);
				}
			}
		}
		
	}

}
