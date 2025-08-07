package common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the Message class for creating messages
 */
public class MessageTest {
    private static final String USER_ID_1 = "UserId1";
    private static final String USER_ID_2 = "UserId2";
    private static final String SERVER_ID = "[SERVER]";
    private static final String GROUP_ID = "Group";
    private static final String EXAMPLE_CONTENT = "Hello world!";

    private void assertMessageEquals(Message.Type type, String sender, String recipient, Object content,
            Message original) {
        assertEquals(type, original.getType());
        assertEquals(sender, original.getSender());
        assertEquals(recipient, original.getRecipient());
        // Different message types are expected to have different content types
        if (content == null) {
            assertNull(original.getContent()); // e.g. USER_JOIN
        } else {
            assertEquals(content, original.getContent()); // e.g. MESSAGE
        }
    }

    @Test
    void constructor_CreatesMessage_IfValidParams() {
        Message message = new Message(Message.Type.MESSAGE, USER_ID_1, USER_ID_2, EXAMPLE_CONTENT);
        assertMessageEquals(Message.Type.MESSAGE, USER_ID_1, USER_ID_2, EXAMPLE_CONTENT, message);
        // Ignore timestamp as it gets set in the constructor, runs on runtime
    }

    @Test
    void requestJoin_ReturnsJoinMessage_IfGivenRequesterId() {
        Message message = Message.requestJoin(USER_ID_1);
        assertMessageEquals(Message.Type.USER_JOIN, USER_ID_1, SERVER_ID, null, message);
    }

    @Test
    void rejectJoin_ReturnsRejectionMessage_IfGivenRecipientId() {
        Message message = Message.rejectJoin(USER_ID_1);
        assertMessageEquals(Message.Type.REJECT_USER_JOIN, SERVER_ID, USER_ID_1, null, message);
    }

    @Test
    void openPrivateChat_ReturnsOpenChatMessage_IfGivenSenderAndTarget() {
        Message message = Message.openPrivateChat(USER_ID_1, USER_ID_2);
        assertMessageEquals(Message.Type.OPEN_PRIVATE_CHAT, USER_ID_1, SERVER_ID, USER_ID_2, message);
    }

    @Test
    void closePrivateChat_ReturnsCloseChatMessage_IfGivenSenderId() {
        Message message = Message.closePrivateChat(USER_ID_1);
        assertMessageEquals(Message.Type.CLOSE_PRIVATE_CHAT, USER_ID_1, SERVER_ID, null, message);
    }

    @Test
    void requestUserDetails_ReturnsUserDetailsRequestMessage_IfGivenSenderAndTarget() {
        Message message = Message.requestUserDetails(USER_ID_1, USER_ID_2);
        assertMessageEquals(Message.Type.USER_DETAILS_REQUEST, USER_ID_1, SERVER_ID, USER_ID_2, message);
    }

    @Test
    void respondUserDetails_ReturnsUserDetailsResponseMessage_IfGivenRecipientAndDetails() {
        Map<String, String> details = new HashMap<>();
        details.put("userId", USER_ID_2);
        details.put("role", "MEMBER");
        details.put("status", "ACTIVE");

        Message message = Message.respondUserDetails(USER_ID_1, details);
        assertMessageEquals(Message.Type.USER_DETAILS_RESPONSE, SERVER_ID, USER_ID_1, details, message);
    }

    @Test
    void sendMessage_ReturnsMessage_IfGivenSenderRecipientAndContent() {
        Message message = Message.sendMessage(USER_ID_1, USER_ID_2, EXAMPLE_CONTENT);
        assertMessageEquals(Message.Type.MESSAGE, USER_ID_1, USER_ID_2, EXAMPLE_CONTENT, message);
    }

    @Test
    void sendUserList_ReturnsUserListMessage_IfGivenUserMap() {
        Map<String, Map<String, String>> userList = new LinkedHashMap<>();

        Map<String, String> user1Details = new HashMap<>();
        user1Details.put("role", "COORDINATOR");
        user1Details.put("status", "ACTIVE");
        userList.put(USER_ID_1, user1Details);

        Map<String, String> user2Details = new HashMap<>();
        user2Details.put("role", "MEMBER");
        user2Details.put("status", "INACTIVE");
        userList.put(USER_ID_2, user2Details);

        Message message = Message.sendUserList(userList);
        assertMessageEquals(Message.Type.USER_LIST, SERVER_ID, GROUP_ID, userList, message);
    }

    @Test
    void updateStatus_ReturnsStatusUpdateMessage_IfGivenUserId() {
        Message message = Message.updateStatus(USER_ID_1);
        assertMessageEquals(Message.Type.STATUS_UPDATE, SERVER_ID, GROUP_ID, USER_ID_1, message);
    }
}
