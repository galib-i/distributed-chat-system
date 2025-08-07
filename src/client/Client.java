package client;

import javax.swing.SwingUtilities;

import client.controller.ClientController;
import client.model.ConnectionManager;

/**
 * Entry point for the client
 */
public class Client {
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();

        SwingUtilities.invokeLater(() -> new ClientController(connectionManager).loadLogin());
    }
}
