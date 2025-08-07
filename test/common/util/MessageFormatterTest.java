package common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import common.model.Message;

/*
 * Tests the MessageFormatter for formatting and parsing messages
 */
public class MessageFormatterTest {
    private static final String MESSAGE_FORMAT = "type=%s&sender=%s&recipient=%s&content=%s";
    private static final String USER_ID_1 = "UserId1";
    private static final String USER_ID_2 = "UserId2";
    private static final String SERVER_ID = "[SERVER]";
    private static final String GROUP_ID = "Group";
    private static final String EXAMPLE_MESSAGE = "Hello world!";

    private String formatMessage(String type, String sender, String recipient, String content) {
        return MESSAGE_FORMAT.formatted(type, sender, recipient, content);
    }

    private void assertMessageEquals(Message.Type type, String sender, String recipient, Object content,
            Message originalMessage) {
        assertEquals(type, originalMessage.getType());
        assertEquals(sender, originalMessage.getSender());
        assertEquals(recipient, originalMessage.getRecipient());
        assertEquals(content, originalMessage.getContent());
    }

    @Test
    void format_ReturnsFormattedString_IfMessage() {
        Message message = Message.sendMessage(USER_ID_1, GROUP_ID, EXAMPLE_MESSAGE);
        String formatted = MessageFormatter.format(message);

        assertEquals(formatMessage("MESSAGE", USER_ID_1, GROUP_ID, EXAMPLE_MESSAGE), formatted);
    }

    @Test
    void parse_ReturnsMessageObject_IfMessage() {
        String messageString = formatMessage("MESSAGE", USER_ID_1, GROUP_ID, EXAMPLE_MESSAGE);
        Message parsed = MessageFormatter.parse(messageString);

        assertMessageEquals(Message.Type.MESSAGE, USER_ID_1, GROUP_ID, EXAMPLE_MESSAGE, parsed);
    }

    @Test
    void parse_ReturnsMessageObject_IfHasMapContent() {
        String messageString = formatMessage("USER_DETAILS_RESPONSE", SERVER_ID, USER_ID_1,
                "userId=%s,role=MEMBER,status=ACTIVE".formatted(USER_ID_1));
        Message parsed = MessageFormatter.parse(messageString);

        @SuppressWarnings("unchecked") // The message type is known to be a map
        Map<String, String> content = (Map<String, String>) parsed.getContent();

        assertEquals(Message.Type.USER_DETAILS_RESPONSE, parsed.getType());
        assertEquals(USER_ID_1, content.get("userId"));
        assertEquals("MEMBER", content.get("role"));
        assertEquals("ACTIVE", content.get("status"));
    }

    @Test
    void parse_ReturnsMessageObject_IfHasNestedMapContent() {
        String messageString = formatMessage("USER_LIST", SERVER_ID, GROUP_ID,
                "%s={role=COORDINATOR,status=ACTIVE},%s={role=MEMBER,status=INACTIVE}".formatted(USER_ID_1, USER_ID_2));
        Message parsed = MessageFormatter.parse(messageString);

        @SuppressWarnings("unchecked") // The message type is known to be a map
        Map<String, Map<String, String>> content = (Map<String, Map<String, String>>) parsed.getContent();

        assertTrue(content.containsKey(USER_ID_1));
        assertEquals("COORDINATOR", content.get(USER_ID_1).get("role"));
        assertEquals("ACTIVE", content.get(USER_ID_1).get("status"));

        assertTrue(content.containsKey(USER_ID_2));
        assertEquals("MEMBER", content.get(USER_ID_2).get("role"));
        assertEquals("INACTIVE", content.get(USER_ID_2).get("status"));
    }
}
