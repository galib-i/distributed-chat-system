package server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.model.Message;
import common.util.MessageFormatter;
import server.model.User;
import server.model.UserManager;

/**
 * Tests the MessageController for user interactions and checking messages.
 * Uses PrintWriter with StringWriter to mock I/O operations without real sockets
 */
public class MessageControllerTest {
    private MessageController messageController;
    private UserManager userManager;
    private StringWriter stringWriter1;
    private StringWriter stringWriter2;
    private PrintWriter printWriter1;
    private PrintWriter printWriter2;

    private static final String USER_ID_1 = "User1";
    private static final String USER_ID_2 = "User2";
    private static final String SERVER_ID = "[SERVER]";
    private static final String GROUP_ID = "Group";
    private static final String SOCKET_ADDRESS = "127.0.0.1:1549";
    private static final String EXAMPLE_MESSAGE = "Hello World!";

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        messageController = new MessageController(userManager);

        stringWriter1 = new StringWriter();
        stringWriter2 = new StringWriter();
        printWriter1 = new PrintWriter(stringWriter1, true);
        printWriter2 = new PrintWriter(stringWriter2, true);

        userManager.addUser(new User(USER_ID_1, SOCKET_ADDRESS, printWriter1));
        userManager.addUser(new User(USER_ID_2, SOCKET_ADDRESS, printWriter2));
    }

    private String getOutput(StringWriter writer) {
        return writer.toString();
    }

    private Message parseMessage(String output) {
        return MessageFormatter.parse(output);
    }

    private void assertOutputContains(String output, String... expectedTexts) {
        for (String text : expectedTexts) {
            assertTrue(output.contains(text), text);
        }
    }

    private void assertOutputContainsForBothUsers(String... expectedTexts) {
        String output1 = getOutput(stringWriter1);
        String output2 = getOutput(stringWriter2);

        for (String text : expectedTexts) {
            assertTrue(output1.contains(text), text);
            assertTrue(output2.contains(text), text);
        }
    }

    @Test
    void controlUserJoin_SendsJoinMessageAndUserList_IfUserJoins() {
        messageController.controlUserJoin(USER_ID_1);
        String output = getOutput(stringWriter1);

        assertOutputContains(output, "%s has joined the chat".formatted(USER_ID_1), "USER_LIST", "is the coordinator");
    }

    @Test
    void controlUserLeave_SendsLeaveMessageAndUserList_IfUserLeaves() {
        messageController.controlUserLeave(USER_ID_2, false);
        String output = getOutput(stringWriter1);

        assertOutputContains(output, "%s has left the chat".formatted(USER_ID_2), "USER_LIST");
    }

    @Test
    void controlUserLeave_AnnouncesNewCoordinator_IfCoordinatorLeaves() {
        messageController.controlUserLeave(USER_ID_1, true);
        String output = getOutput(stringWriter2);

        assertOutputContains(output, "The old coordinator", "is the new coordinator");
    }

    @Test
    void sendMessage_SendsToAllUsers_IfSentMessage() {
        messageController.sendMessage(USER_ID_1, GROUP_ID, EXAMPLE_MESSAGE);
        assertOutputContainsForBothUsers(EXAMPLE_MESSAGE);
    }

    @Test
    void sendMessage_SendsToSenderAndRecipient_IfSentPrivateMessage() {
        messageController.sendMessage(USER_ID_1, USER_ID_2, EXAMPLE_MESSAGE);
        assertOutputContainsForBothUsers(EXAMPLE_MESSAGE);
    }

    @Test
    void sendUserDetails_SendsUserDetailsToRequester_IfRequestedDetails() {
        messageController.sendUserDetails(USER_ID_1, USER_ID_2);
        String output = getOutput(stringWriter1);
        Message parsedMessage = parseMessage(output);

        assertEquals(Message.Type.USER_DETAILS_RESPONSE, parsedMessage.getType());
        assertEquals(SERVER_ID, parsedMessage.getSender());

        @SuppressWarnings("unchecked") // The message type is known to be a map
        Map<String, String> details = (Map<String, String>) parsedMessage.getContent();
        assertEquals(USER_ID_2, details.get("userId"));
    }

    @Test
    void controlCommunication_ProcessesMessageType_IfReceivedMessage() {
        Message message = Message.sendMessage(USER_ID_1, USER_ID_2, EXAMPLE_MESSAGE);
        messageController.controlCommunication(USER_ID_1, message);
        String output = getOutput(stringWriter2);

        assertOutputContains(output, EXAMPLE_MESSAGE);
    }

    @Test
    void controlStatusUpdate_TogglesUserStatusAndSendsUserList_IfReceivedStatusUpdate() {
        messageController.controlStatusUpdate(USER_ID_1);
        String output = getOutput(stringWriter1);
        assertOutputContains(output, "USER_LIST", "INACTIVE");
    }

    @Test
    void openPrivateChat_SendsOpenPrivateChatRequest_IfUserRequested() {
        messageController.openPrivateChat(USER_ID_1, USER_ID_2);
        String output = getOutput(stringWriter2);
        Message parsedMessage = parseMessage(output);

        assertEquals(Message.Type.OPEN_PRIVATE_CHAT, parsedMessage.getType());
        assertEquals(USER_ID_1, parsedMessage.getSender());
    }

    @Test
    void broadcastUserList_SendsUserListToAllUsers_IfCalled() {
        messageController.broadcastUserList();
        assertOutputContainsForBothUsers("USER_LIST");
    }

}
