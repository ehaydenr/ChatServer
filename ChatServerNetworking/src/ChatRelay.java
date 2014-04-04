/**
 * Allows chat specific functions to occurr before message is relayed
 * @author ehaydenr
 *
 */
public class ChatRelay extends Relay {

	public ChatRelay(NetworkHandler server) {
		super(server);
	}

	@Override
	public void priorToSendingOutgoing() {
		// Chat relay doesn't really need to do anything. Maybe a profanity filter?
		
	}

}
