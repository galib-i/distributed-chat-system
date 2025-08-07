package server.controller;

import java.util.Map;

import common.model.Message;
import common.util.MessageFormatter;
import server.model.User;
import server.model.UserManager;

/**
 * Controls the messages sent between the server and clients
 */
public class MessageController {
    private final UserManager userManager;
    private static final String SERVER_ID = "[SERVER]";
    private static final String GROUP_ID = "Group";

    public MessageController(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * When a user joins the server, announce join, update user list and notify user
     * of coordinator
     * 
     * @param userId Id of the user that joined
     */
    public void controlUserJoin(String userId) {
        broadcastSystemMessage("%s has joined the chat.".formatted(userId));
        broadcastUserList();
        String coordinatorId = userManager.getCoordinatorId();
        notifyUser(null, userId, "%s is the coordinator.".formatted(coordinatorId));
    }

    /**
     * When a user leaves the server, announce leave, update user list and if was
     * coordinator, announce new one
     * 
     * @param userId        Id of the user that left
     * @param isCoordinator True: user was the coordinator
     */
    public void controlUserLeave(String userId, boolean isCoordinator) {
        broadcastSystemMessage("%s has left the chat.".formatted(userId));

        if (isCoordinator) {
            broadcastSystemMessage("The old coordinator, %s, has left the chat. %s is the new coordinator."
                    .formatted(userId, userManager.getCoordinatorId()));
        }
        broadcastUserList();

        sendMessageToGroup(Message.closePrivateChat(userId));
    }

    public void controlStatusUpdate(String userId) {
        userManager.toggleUserStatus(userId);
        broadcastUserList();
    }

    public void openPrivateChat(String senderId, String targetUserId) {
        sendMessageToUser(targetUserId, Message.openPrivateChat(senderId, targetUserId));
    }

    /**
     * Sends a message to everyone or displays for both sender and recipient in a
     * private chat
     * 
     * @param sender    Id of the user or server sending the message
     * @param recipient Id of the user or "Group" receiving the message
     * @param content   Message content
     */
    public void sendMessage(String sender, String recipient, String content) {
        Message message = Message.sendMessage(sender, recipient, content);
        String formattedMessage = MessageFormatter.format(message);

        if (recipient.equals(GROUP_ID)) {
            broadcastMessage(formattedMessage);
        } else {
            sendPrivateMessage(sender, recipient, formattedMessage);
        }
    }

    private void sendPrivateMessage(String sender, String recipient, String formattedMessage) {
        User recipientUser = userManager.getUser(recipient);
        if (recipientUser != null) {
            recipientUser.getWriter().println(formattedMessage);
        }

        if (!sender.equals(SERVER_ID)) { // Send to both sender and recipients
            User senderUser = userManager.getUser(sender);
            if (senderUser != null) {
                senderUser.getWriter().println(formattedMessage);
            }
        }
    }

    public void sendUserDetails(String requesterId, String targetId) {
        Map<String, String> details = userManager.getUserDetails(targetId, true);
        sendMessageToUser(requesterId, Message.respondUserDetails(targetId, details));
    }

    public void broadcastUserList() {
        sendMessageToGroup(Message.sendUserList(userManager.getAllUserDetails()));
    }

    public void notifyUser(String sender, String recipient, String content) {
        if (sender == null) {
            sender = SERVER_ID;
        }

        sendMessageToUser(recipient, Message.sendMessage(sender, recipient, content));
    }

    private void broadcastSystemMessage(String content) {
        sendMessage(SERVER_ID, GROUP_ID, content);
    }

    private void sendMessageToUser(String userId, Message message) {
        User user = userManager.getUser(userId);
        if (user != null) {
            String formattedMessage = MessageFormatter.format(message);
            user.getWriter().println(formattedMessage);
        }
    }

    private void sendMessageToGroup(Message message) {
        broadcastMessage(MessageFormatter.format(message));
    }

    public void broadcastMessage(String content) {
        for (User user : userManager.getUsers()) {
            user.getWriter().println(content);
        }
    }

    /**
     * Controls the communication between the client and server
     * 
     * @param userId  The user who sent the message
     * @param message The message object to process
     */
    public void controlCommunication(String userId, Message message) {
        switch (message.getType()) {
            case MESSAGE -> {
                String recipient = message.getRecipient();
                String content = (String) message.getContent();
                sendMessage(userId, recipient, content);
            }
            case OPEN_PRIVATE_CHAT -> {
                String targetUserId = (String) message.getContent();
                openPrivateChat(userId, targetUserId);
            }
            case USER_DETAILS_REQUEST -> {
                String targetId = (String) message.getContent();
                sendUserDetails(userId, targetId);
            }
            case STATUS_UPDATE -> controlStatusUpdate(userId);
            default -> {
            }
        }
    }
}
