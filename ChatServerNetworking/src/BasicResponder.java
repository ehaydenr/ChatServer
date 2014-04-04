
public class BasicResponder extends Responder {

	@Override
	public void clientRecieved(ServerClient client) {
		// TODO Auto-generated method stub
		Printer.println(this.getClass(), false, "We got a new Client! - " + client.getUsername());
	}

	@Override
	public void clientDisconnected(ServerClient client) {
		// Client connection has already been closed. Information remains
		Printer.println(this.getClass(), false, "Client disconnected - " + client.getUsername());
	}

	@Override
	public void capOfClientsReached() {
		// TODO Auto-generated method stub
		Printer.println(this.getClass(), false, "Cap of clients reached");
	}

	@Override
	public void appliedMessageRecieved(Message message) {
		// TODO Auto-generated method stub
		Printer.println(this.getClass(), false, "Applied message received: " + message.toString());
	}

	@Override
	public Message getGoodbyeMessage(ServerClient client) {
		// TODO Auto-generated method stub
		Message goodbye = new Message(MessageType.CHAT, new Integer(-1), client.getClientId(),
				"Server", client.getUsername(), "GoodBye!");
		return goodbye;
	}
	
	@Override
	public Message getWelcomeMessage(ServerClient client){
		Message welcome = new Message(MessageType.CHAT, new Integer(-1), client.getClientId(),
				"Server", client.getUsername(), "Welcome to my chat server!");
		return welcome;
	}

	@Override
	public void chatMessageSent(Message message) {
		Printer.println(this.getClass(), false, "Chat room: " + message.toString());
	}

}
