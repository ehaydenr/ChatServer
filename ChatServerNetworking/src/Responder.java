
public abstract class Responder {
	public abstract void clientRecieved(ServerClient client);
	public abstract void clientDisconnected(ServerClient client);
	public abstract void capOfClientsReached();
	public abstract void appliedMessageRecieved(Message message);
	public Message getGoodbyeMessage(ServerClient client){
		return null;
	}
	public Message getWelcomeMessage(ServerClient client){
		return null;
	}
	public abstract void chatMessageSent(Message message);
}
