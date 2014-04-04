import java.util.Date;

/**
 * Message Object:

 Notes
 In current state, not to be serialized because it has clientIDs

 Message Types
 System
 Heartbeat, quit, etc.
 Chat
 Full chat room
 Private client to client chat
 Applied
 Whatever the developer using this jar wants

 Instance Fields
 Enum Message Type
 Sender id
 Recipient id		// Sender and recipient IDs should be converted to usernames at a lower level before sending
 String content
 Date/Time sent

 Methods (Besides getters and setters)
 toString representation for sending to clients
 toStringServer representation for the server
 * @author ehaydenr
 *
 */
import com.google.gson.Gson;

public class Message {

	private MessageType type;
	private Integer senderId, recipientId;
	private String content, senderUsername, recipientUsername;
	private Date timeSent;

	/*
	 * -1 recipient id will be interpreted as public -1 sender id will be
	 * interpreted as server
	 */
	public Message(MessageType type, Integer senderId, Integer recipientId,
			String senderUsername, String recipientUsername, String content) {

		this.type = type;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.senderUsername = senderId != null && senderId.equals(-1) ? "Server" : senderUsername;
		this.recipientUsername = recipientId != null && recipientId.equals(-1) ? "Everyone"
				: recipientUsername;
		this.content = content;
		this.timeSent = new Date();

	}
	
	public static void assembleClientMessage(Message message, Integer recipientId, String recipientUsername){
		message.recipientId = recipientId;
		message.recipientUsername = recipientUsername;
	}

	public String toString() {
		Gson gson = new Gson();
		// TODO: remove client and recipient ID's clients should not know this
		return gson.toJson(this);
	}

	public void setTimeSent(Date timeSent) {
		this.timeSent = timeSent;
	}

	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}

	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public void setRecipientUsername(String recipientUsername) {
		this.recipientUsername = recipientUsername;
	}

	public MessageType getType() {
		return type;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public Integer getRecipientId() {
		return recipientId;
	}

	public String getContent() {
		return content;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public String getRecipientUsername() {
		return recipientUsername;
	}

	public Date getTimeSent() {
		return timeSent;
	}

}
