package common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.model.Message;

/**
 * Formats and parses messages as strings rather than objects (string serialisation):
 * create message type object -> format message object to string -> send string -> parse string to message object.
 * Could be removed or simplified by using a library for strings, or direct objects
 */
public class MessageFormatter {
    private static final String MESSAGE_FORMAT = "type=%s&sender=%s&recipient=%s&content=%s";
    private static final String KEY_VALUE_PATTERN = "(\\w+)=([^,]+)"; // Key-value pairs from String[] e.g. [key1=value1, key2=value2]
    private static final String NESTED_MAP_PATTERN = "(\\w+)=\\{(.*?)}"; // Nested maps e.g. {key1={keyA=valueB, keyA=valueB}, key2.. }

    /**
     * Formats a message object into a string to be sent over the server
     * @param message Message object to be formatted
     * @return String formatted message
     */
    public static String format(Message message) {
        return MESSAGE_FORMAT.formatted(message.getType(), message.getSender(), message.getRecipient(), message.getContent());
    }

    /**
     * Parses a string message back into a message object, if map, parse into map
     * @param messageString String message to be parsed
     * @return Message object
     */
    public static Message parse(String messageString) {
        String[] parts = messageString.split("&", 4);

        Message.Type type = Message.Type.valueOf(parts[0].replace("type=", ""));
        String sender = parts[1].replace("sender=", "");
        String recipient = parts[2].replace("recipient=", "");
        String content = parts[3].replace("content=", "");

        Object parsedContent = switch (type) {
            case USER_LIST -> extractStringToMap(content, NESTED_MAP_PATTERN, true);
            case USER_DETAILS_RESPONSE -> extractStringToMap(content, KEY_VALUE_PATTERN, false);
            default -> content;
        };

        return new Message(type, sender, recipient, parsedContent);
    }

    private static Map<String, ?> extractStringToMap(String input, String regex, boolean isNested) {
        Map<String, Object> map = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2).replaceAll("}$", ""); // Removes trailing "}" from non-nested maps

            if (isNested) {
                map.put(key, extractStringToMap(value, KEY_VALUE_PATTERN, false));
            } else {
                map.put(key, value);
            }
        }

        return map;
    }
}
