import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class IOStreamsHandler extends Thread {
	private Queue<Message> incoming, outgoing;
	private InputStreamReader inputStream;
	private OutputStreamWriter outputStream;
	private Client client;
	private Socket connection;

	public IOStreamsHandler(Client client, Socket connection) {
		this.client = client;
		this.connection = connection;
		this.incoming = new LinkedList<Message>();
		this.outgoing = new LinkedList<Message>();

		try {
			// Currently sending Json Strings
			inputStream = new InputStreamReader(new BufferedInputStream(
					connection.getInputStream()), "ASCII");
			outputStream = new OutputStreamWriter(new BufferedOutputStream(
					connection.getOutputStream()), "ASCII");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		while (!this.isInterrupted()) {

			// Writing
			synchronized (this.outgoing) {
				while (!this.outgoing.isEmpty() && !this.isInterrupted()) {
					Message mOut = this.outgoing.remove();
					try {
						outputStream.write(mOut.toString());
						outputStream.flush();
						Printer.println(this.getClass(), false, "Message sent to client: " + mOut.toString());
					} catch (IOException e) {
						Printer.println(this.getClass(), true, "Error writing to client. Closing. - " + this.client.getUsername());
						if(!this.isInterrupted())
							client.closeClient();
					}
				}
			}

			// Reading
			StringBuilder sb = new StringBuilder();
			String result = "";

			try {
				char c;
				while (inputStream.ready()
						&& (c = (char) inputStream.read()) != '\n' && !this.isInterrupted()) {
					// May need to be adapted for other OS's
					sb.append((char) c);
				}
				if (!sb.toString().equals(""))
					result = sb.toString().replace("\r", "");	//May need to be changed to accommodate for other operating systems

			} catch (IOException e) {
				Printer.println(this.getClass(), true, "Error reading from client. Closing. - " + this.client.getUsername());
				if(!this.isInterrupted())
					client.closeClient();
			}

			if (!result.equals("")) {
				try {
					Gson gson = new Gson();
					Printer.println(this.getClass(), false, "Trying to convert: " + result);
					Message mIn = gson.fromJson(result, Message.class);
					
					if(mIn == null) throw new JsonParseException("Message null");
					
					// Forgery prevention
					mIn.setSenderId(client.getClientId());
					mIn.setSenderUsername(client.getUsername());
					mIn.setTimeSent(new Date());
					if (mIn.getRecipientId().equals(-1))
						mIn.setRecipientUsername("Everyone");
					
					this.incoming.add(mIn);
					
					Printer.println(this.getClass(), false, "Message read from client: " + mIn.toString());
				} catch (JsonParseException e) {
					Printer.println(this.getClass(), true, "Couldn't parse incoming message to json. Client: " + this.client.getUsername());
				}
			}
		}
	}

	/**
	 * Close should only be called form the Client Class.
	 * Flush and close streams, softly end thread
	 */
	public void close() {
		Printer.println(this.getClass(), false, "Close called, interrupting.");
		this.interrupt();
		Printer.println(this.getClass(), false, "Interrupted Status: " + this.isInterrupted());
		try {
			inputStream.close();
			outputStream.flush();
			outputStream.close();
			connection.close();
		} catch (IOException e) {
			Printer.println(this.getClass(), true, "Error closing streams or connection. Client: " + this.client.getUsername());
			this.interrupt();
		}
	}

	/**
	 * Write directly if urgent, otherwise add message to outgoing queue
	 * 
	 * @param message - Message to be sent
	 * @param urgent - whether or not it should block
	 */
	public void post(Message message, boolean urgent) {
		// Won't conncurrently process output stream or add to list
		synchronized (this.outgoing) {
			if (urgent)
				try {
					this.outputStream.write(message.toString());
					this.outputStream.flush();
				} catch (IOException e) {
					if(!this.isInterrupted())
						e.printStackTrace();
				}
			else
				this.outgoing.add(message);
		}
	}

	/**
	 * @return message received or null if no messages
	 */
	public Message pull() {
		synchronized (this.incoming) {
			if (!this.incoming.isEmpty()) {
				try {
					return incoming.remove();
				} catch (JsonParseException e) {
					Printer.println(this.getClass(), true, "Couldn't parse incoming message to json. Client: " + this.client.getUsername());
				}
			}
		}
		return null;
	}
}
