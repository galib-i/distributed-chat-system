package common.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Different messages that can be sent between the client and server for both actual chat messages and control messages
 */
public class Message {
    public enum Type {
        USER_JOIN, REJECT_USER_JOIN,
        OPEN_PRIVATE_CHAT, CLOSE_PRIVATE_CHAT,
        USER_DETAILS_REQUEST, USER_DETAILS_RESPONSE,
        MESSAGE,
        USER_LIST,
        STATUS_UPDATE,
    }

    private static final String SERVER_ID = "[SERVER]";
    private static final String GROUP_ID = "Group";
    private final Type type;
    private final String timestamp;
    private final String sender;
    private final String recipient;
    private final Object content;

    /**
     * Creates a new message object with:
     * 
     * @param type      Message type (e.g. user join, message)
     * @param sender    Id of the sender or from [SERVER]
     * @param recipient Id of the recipient or to everyone in the "Group"
     * @param content   Content of the message based on the type
     */
    public Message(Type type, String sender, String recipient, Object content) {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public Type getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public Object getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static Message requestJoin(String requesterId) {
        return new Message(Type.USER_JOIN, requesterId, SERVER_ID, null);
    }

    public static Message rejectJoin(String recipientId) {
        return new Message(Type.REJECT_USER_JOIN, SERVER_ID, recipientId, null);
    }

    public static Message openPrivateChat(String senderId, String targetId) {
        return new Message(Type.OPEN_PRIVATE_CHAT, senderId, SERVER_ID, targetId);
    }

    public static Message closePrivateChat(String senderId) {
        return new Message(Type.CLOSE_PRIVATE_CHAT, senderId, SERVER_ID, null);
    }

    public static Message requestUserDetails(String senderId, String targetId) {
        return new Message(Type.USER_DETAILS_REQUEST, senderId, SERVER_ID, targetId);
    }

    public static Message respondUserDetails(String recipientId, Map<String, String> details) {
        return new Message(Type.USER_DETAILS_RESPONSE, SERVER_ID, recipientId, details);
    }

    public static Message sendMessage(String sender, String recipient, String content) {
        return new Message(Type.MESSAGE, sender, recipient, content);
    }

    public static Message sendUserList(Map<String, Map<String, String>> userList) {
        return new Message(Type.USER_LIST, SERVER_ID, GROUP_ID, userList);
    }

    public static Message updateStatus(String userId) {
        return new Message(Type.STATUS_UPDATE, SERVER_ID, GROUP_ID, userId);
    }

}
