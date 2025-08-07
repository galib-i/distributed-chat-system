package server.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.net.Socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.model.UserManager;

/**
 * Tests the ConnectionController for new socket connections through a mock.
 */
public class ConnectionControllerTest {
    private ConnectionController connectionController;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        connectionController = new ConnectionController(userManager);
    }

    @Test
    void constructor_CreatesConnectionController_IfGivenUserManager() {
        assertDoesNotThrow(() -> new ConnectionController(userManager));
    }

    @Test
    void handleNewConnection_StartsNewThread_IfGivenSocket() {
        Socket mockSocket = null;
        assertDoesNotThrow(() -> connectionController.handleNewConnection(mockSocket));
    }
}
