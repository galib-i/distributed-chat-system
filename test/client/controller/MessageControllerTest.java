package client.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.model.ConnectionManager;
import client.view.ChatWindowView;
import common.model.Message;

/**
 * Tests the MessageController for communication using PrintWriter with
 * StringWriter to mock I/O operations without real sockets
 */
public class MessageControllerTest {
    private MessageController messageController;
    private ConnectionManager connectionManager;
    private ChatWindowView chatWindowView;

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager();
        chatWindowView = new ChatWindowView();
        messageController = new MessageController(connectionManager, chatWindowView, null);
    }

    @Test
    void controlCommunication_UpdatesUserList_IfReceivedUserListMessage() {
        Map<String, Map<String, String>> userList = new HashMap<>();
        Map<String, String> userDetails = new HashMap<>();

        userDetails.put("role", "MEMBER");
        userDetails.put("status", "ACTIVE");
        userList.put("User1", userDetails);

        Message message = new Message(Message.Type.USER_LIST, "[SERVER]", "Group", userList);
        messageController.controlCommunication(message);

        assertTrue(chatWindowView.getUserListView().getComponents().length > 0);
    }

    @Test
    void controlCommunication_DisplaysMessage_IfReceivedMessage() {
        Message message = new Message(Message.Type.MESSAGE, "User1", "Group", "Hello World!");
        messageController.controlCommunication(message);

        String chatName = chatWindowView.getChatView().getCurrentChatName();
        assertEquals("Group", chatName);
    }

    @Test
    void controlCommunication_OpensPrivateChat_IfReceivedOpenPrivateChatMessage() {
        Message message = new Message(Message.Type.OPEN_PRIVATE_CHAT, "User1", "Group", null);
        messageController.controlCommunication(message);

        assertTrue(chatWindowView.getChatView().getComponents().length > 0); // If private chat tabs are opened
    }

    @Test
    void controlCommunication_ClosesPrivateChat_IfReceivedClosePrivateChatMessage() {
        chatWindowView.getChatView().openPrivateChat("User1");
        Message message = new Message(Message.Type.CLOSE_PRIVATE_CHAT, "User1", "Group", null);
        messageController.controlCommunication(message);

        assertEquals(1, chatWindowView.getChatView().getComponents().length); // Only the "Group" tab
    }
}