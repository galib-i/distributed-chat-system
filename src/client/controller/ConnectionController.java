package client.controller;

import java.io.IOException;
import java.net.ConnectException;

import client.model.ConnectionManager;
import client.view.LoginView;

/**
 * Connects the login view and the model to the server
 */
public class ConnectionController {
    private final LoginView view;
    private final ClientController clientController;

    public ConnectionController(ConnectionManager model, LoginView view, ClientController clientController) {
        this.view = view;
        this.clientController = clientController;

        view.connectButtonAction(e -> requestConnection());
    }

    private void requestConnection() {
        LoginView.ConnectionDetails details = view.getConnectionDetails();

        try {
            clientController.loadChatWindow(details.userId(), details.serverIp(), details.serverPort());
        } catch (ConnectException e) {
            view.showErrorMessage("Connection refused!\n(Is the chat server running?)");
        } catch (IllegalArgumentException | IOException e) {
            view.showErrorMessage(e.getMessage());
        }
    }
}