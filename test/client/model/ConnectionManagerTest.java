package client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.ConnectException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ConnectionManager for connecting to a server and validating user input
 */
public class ConnectionManagerTest {
    private ConnectionManager connectionManager;
    private final String VALID_USER_ID = "UserId1";
    private final String VALID_SERVER_IP = "localhost";
    private final String VALID_SERVER_PORT = "1549";

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager();
    }

    private void assertIllegalArgumentException(String userId, String serverIp, String serverPort,
            String expectedMessage) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> connectionManager.connect(userId, serverIp, serverPort));
        assertEquals(expectedMessage, e.getMessage());
    }

    // userId tests
    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithInvalidUserId() {
        assertIllegalArgumentException("", VALID_SERVER_IP, VALID_SERVER_PORT, "All fields are required!");
        assertIllegalArgumentException("#UserId!!!", VALID_SERVER_IP, VALID_SERVER_PORT,
                "User ID must be alphanumeric!");
    }

    // serverIp tests
    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithInvalidServerIp() {
        assertIllegalArgumentException(VALID_USER_ID, "", VALID_SERVER_PORT, "All fields are required!");
        assertIllegalArgumentException(VALID_USER_ID, "127.0.0", VALID_SERVER_PORT, "Invalid IP address!");
        assertIllegalArgumentException(VALID_USER_ID, "256.256.256.256", VALID_SERVER_PORT, "Invalid IP address!");
        assertIllegalArgumentException(VALID_USER_ID, "serverIp", VALID_SERVER_PORT, "Invalid IP address!");
    }

    // serverPort tests
    @Test
    void connect_ThrowsIllegalArgumentException_IfConnectWithInvalidServerPort() {
        assertIllegalArgumentException(VALID_USER_ID, VALID_SERVER_IP, "", "All fields are required!");
        assertIllegalArgumentException(VALID_USER_ID, VALID_SERVER_IP, "-1", "Port must be between 0 and 65535!");
        assertIllegalArgumentException(VALID_USER_ID, VALID_SERVER_IP, "65536", "Port must be between 0 and 65535!");
    }

    // Connection tests
    @Test
    void connect_ThrowsConnectException_IfConnnectToOfflineServer() {
        assertThrows(ConnectException.class,
                () -> connectionManager.connect(VALID_USER_ID, VALID_SERVER_IP, VALID_SERVER_PORT));
    }

    @Test
    void connect_ThrowsIllegalArgumentException_IfUserIdAlreadyInUse() throws Exception {
        ConnectionManager connectionManager = new ConnectionManager() {
            @Override
            protected void connectToServer(String serverIp, String serverPort) {
            }

            @Override
            protected void authenticateUser() throws IllegalArgumentException {
                throw new IllegalArgumentException("User ID already in use!");
            }
        };

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> connectionManager.connect(VALID_USER_ID, VALID_SERVER_IP, VALID_SERVER_PORT));
        assertEquals("User ID already in use!", e.getMessage());
    }
}
