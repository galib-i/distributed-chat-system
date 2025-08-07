package client.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.model.ConnectionManager;
import client.view.LoginView;

/**
 * Tests the ConnectionController for the server side connection between the server and a client
 */
public class ConnectionControllerTest {
    private ConnectionController connectionController;
    private ConnectionManager connectionManager;
    private LoginView loginView;
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager();
        loginView = new LoginView();
        clientController = new ClientController(connectionManager);
        connectionController = new ConnectionController(connectionManager, loginView, clientController);
    }

    @Test
    void constructor_CreatesConnectionController_IfGivenValidParams() {
        assertDoesNotThrow(() -> new ConnectionController(connectionManager, loginView, clientController));
    }

}