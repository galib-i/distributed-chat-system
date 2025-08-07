package client.controller;

import java.io.IOException;

import client.model.ActivityModel;
import client.model.ConnectionManager;
import client.model.LostConnectionListener;
import client.view.ChatWindowView;
import client.view.LoginView;

/**
 * Connects the client views and models to the server acts as the main controller for the client
 */
public class ClientController {
    private final ConnectionManager connectionManager;
    private final LoginView loginView;
    private final ChatWindowView chatWindowView;
    private final ActivityModel activityModel;

    public ClientController(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.loginView = new LoginView();
        this.chatWindowView = new ChatWindowView();
        this.activityModel = new ActivityModel(connectionManager);
    }

    public void loadLogin() {
        new ConnectionController(connectionManager, loginView, this);
        loginView.setVisible(true);
    }

    public void loadChatWindow(String userId, String serverIp, String serverPort) throws IOException {
        chatWindowView.updateCurrentServerLabel(serverIp, serverPort);
        chatWindowView.quitButtonAction(e -> quitClient());

        ActivityController activityController = new ActivityController(activityModel, chatWindowView);
        new MessageController(connectionManager, chatWindowView, activityController);

        // Create connection listener in the controller
        connectionManager.setLostConnectionListener(createConnectionListener());

        connectionManager.connect(userId, serverIp, serverPort);

        chatWindowView.setVisible(true);
        loginView.dispose();
    }

    /**
     * Creates a connection listener to handle connection events
     */
    private LostConnectionListener createConnectionListener() {
        return new LostConnectionListener() {
            @Override
            public void onLostConnection(boolean attemptReconnection) {
                handleLostConnection(attemptReconnection);
            }

            @Override
            public void onReconnectionSuccess() {
                chatWindowView.getChatView().displayReconnectedMessage();
                chatWindowView.enableAfterReconnection();
            }
        };
    }

    private void handleLostConnection(boolean attemptReconnection) {
        if (attemptReconnection) {
            chatWindowView.disableForReconnectAttempt();
        } else {
            chatWindowView.showLostConnectionMessage();
            quitClient();
        }
    }

    private void quitClient() {
        connectionManager.disconnect();
        chatWindowView.dispose();
        System.exit(0);
    }
}