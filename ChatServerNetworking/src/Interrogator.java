import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Interrogator Thread
 * 
 * Notes Client connections have already been established, instantiate client
 * object and interrogate connection to verify you want it in the main pool.
 * Have Request handler pass in reference to network manager. Call add in there.
 * 
 * Fields Pool of connections
 * 
 * Execution Interrogate, reject or accept at add to Network manager, remove
 * from pool
 * 
 * Methods Add client to pool
 * 
 * @author ehaydenr
 *
 */
public class Interrogator extends Thread {
	private NetworkHandler server;
	private List<ServerClient> pool, prompted;
	private Queue<ServerClient> rejected, accepted;

	private final long timeExpiration = 3000L; // 3 seconds??
	private Message prompt;

	public Interrogator(NetworkHandler server) {
		this.server = server;
		this.pool = new ArrayList<ServerClient>();
		this.prompted = new ArrayList<ServerClient>();
		this.rejected = new LinkedList<ServerClient>();
		this.accepted = new LinkedList<ServerClient>();
		this.prompt = new Message(MessageType.SYSTEM, new Integer(-1), null,
				"Server", null, "Enter Username");
	}

	public void addConnection(Socket connection) {
		synchronized (pool) {
			ServerClient client = new ServerClient(connection, server);
			this.pool.add(client);
			Printer.println(this.getClass(), false, "New Connection added to pool: " + client.getClientId());
		}
	}

	public void run() {
		while (!this.isInterrupted())
			synchronized (this.pool) {
				for (ServerClient client : pool) {
					if (!prompted.contains(client)) {
						// Assemble Prompt
						Message.assembleClientMessage(prompt, client.getClientId(), client.getUsername());
						client.write(prompt, false);
						prompted.add(client);
						Printer.println(this.getClass(), false, "Client prompted: " + client.getClientId());
					}

					// Check expiration
					Date currentDate = new Date();
					Message read = client.read();
					long timeDifference = currentDate.getTime()
							- client.getRequested().getTime();
					boolean usernameFound = read != null
							&& !read.getContent().equals("");
					if (!usernameFound && timeDifference > timeExpiration) {
						// Remove client from interrogation
						rejected.add(client);
						Printer.println(this.getClass(), false, "Client rejected: " + client.getClientId());
					} else if (usernameFound) {
						// For now this is the only criteria to get accepted -
						// Accept
						client.setUsername(read.getContent());
						accepted.add(client);
						Printer.println(this.getClass(), false, "Client accepted: " + client.getClientId());
					}
				}

				// Process rejects and accepts
				while (!rejected.isEmpty()) {
					ServerClient reject = rejected.remove();
					pool.remove(reject);
					reject.closeClient();
				}
				while (!accepted.isEmpty()) {
					ServerClient accept = accepted.remove();
					accept.setAccepted(new Date());
					
					// Add client to server
					server.addClient(accept);

					// Remove client from interrogation pool
					pool.remove(accept);

				}
			}
	}
}
