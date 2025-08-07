package client.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.model.ConnectionManager;

/**
 * Tests the ClientController for loading the login view
 */
public class ClientControllerTest {
    private ConnectionManager connectionManager;
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager();
        clientController = new ClientController(connectionManager);
    }

    @Test
    void constructor_CreatesClientController_IfGivenConnectionManager() {
        assertDoesNotThrow(() -> new ClientController(connectionManager));
    }

    @Test
    void loadLogin_DoesNotThrowException_IfCalled() {
        assertDoesNotThrow(() -> clientController.loadLogin());
    }
}