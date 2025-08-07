package client.util;

/**
 * Validates the inputs during login: user id, server ip and port
 */
public class LoginInputValidator {
    private static final String USER_ID_PATTERN = "[A-Za-z0-9]+";
    private static final String IP_ADDRESS_PATTERN = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";

    public static void validateConnectionInput(String userId, String serverIp, String serverPort) {
        if (userId.isEmpty() || serverIp.isEmpty() || serverPort.isEmpty()) {
            throw new IllegalArgumentException("All fields are required!");
        }

        if (!userId.matches(USER_ID_PATTERN)) {
            throw new IllegalArgumentException("User ID must be alphanumeric!");
        }

        if (!serverIp.matches(IP_ADDRESS_PATTERN) && !serverIp.equals("localhost")) {
            throw new IllegalArgumentException("Invalid IP address!");
        }

        try {
            int port = Integer.parseInt(serverPort);
            if (!(port > 0 && port <= 65535)) {
                throw new IllegalArgumentException("Port must be between 0 and 65535!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port must be a number!");
        }
    }
}