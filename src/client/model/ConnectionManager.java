package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import client.util.LoginInputValidator;
import common.model.Message;
import common.util.MessageFormatter;

/**
 * Manages the connection between the client and the server, sends and receives communication
 */
public class ConnectionManager {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread messageListenerThread;
    private MessageListener messageListener;
    private LostConnectionListener lostConnectionListener;
    private String userId;
    private int reconnectAttempts = 0;
    private final int MAX_RECONNECT_ATTEMPTS = 3;
    private String lastServerIp;
    private String lastServerPort;

    /**
     * Sends a request to the server, checks if user is unique and starts a message listener thread
     * 
     * @param userId     Id of the user to connect
     * @param serverIp   Server IP address
     * @param serverPort Server port number
     * @throws IllegalArgumentException If input validation fails or user is not
     *                                  unique
     * @throws ConnectException         If server cannot connect (e.g. server is not
     *                                  running)
     * @throws IOException              Handles all other socket exceptions
     */
    public void connect(String userId, String serverIp, String serverPort)
            throws IllegalArgumentException, ConnectException, IOException {
        LoginInputValidator.validateConnectionInput(userId, serverIp, serverPort);
        this.userId = userId;

        // Store server details for reconnection
        this.lastServerIp = serverIp;
        this.lastServerPort = serverPort;

        connectToServer(serverIp, serverPort);
        authenticateUser();

        messageListenerThread = new Thread(this::listenForMessages);
        messageListenerThread.start();
    }

    protected void connectToServer(String serverIp, String serverPort) throws IOException {
        socket = new Socket(serverIp, Integer.parseInt(serverPort));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    protected void authenticateUser() throws IllegalArgumentException, IOException {
        Message joinMessage = Message.requestJoin(userId);
        writer.println(MessageFormatter.format(joinMessage));

        String response = reader.readLine();
        Message responseMsg = MessageFormatter.parse(response);

        if (responseMsg.getType() == Message.Type.REJECT_USER_JOIN) {
            socket.close();
            throw new IllegalArgumentException("User ID already in use!");
        } else {
            processMessage(response);
        }
    }

    public String getUserId() {
        return userId;
    }

    /**
     * Sets the message listener to handle incoming messages
     * 
     * @param listener MessageListener object
     * @see MessageListener
     */
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    /**
     * Sets the disconnection listener to handle server disconnection (server disconnects but client is still running)
     * 
     * @param listener
     * @see LostConnectionListener
     */
    public void setLostConnectionListener(LostConnectionListener listener) {
        this.lostConnectionListener = listener;
    }

    public void sendMessage(String recipient, String message) {
        sendFormattedMessage(Message.sendMessage(userId, recipient, message));
    }

    public void sendUserDetailsRequest(String senderUserId, String targetUserId) {
        sendFormattedMessage(Message.requestUserDetails(userId, targetUserId));
    }

    public void toggleStatus() {
        sendFormattedMessage(Message.updateStatus(userId));
    }

    public void openPrivateChat(String targetUserId) {
        sendFormattedMessage(Message.openPrivateChat(userId, targetUserId));
    }

    private void sendFormattedMessage(Message message) {
        if (message != null) {
            writer.println(MessageFormatter.format(message));
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                processMessage(message);
            }
        } catch (SocketException e) {
            lostConnectionListener.onLostConnection(true);
            handleLostConnection();
        } catch (IOException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
                handleLostConnection();
            }
        }
    }

    private void processMessage(String messageString) {
        Message parsedMessage = MessageFormatter.parse(messageString);
        if (parsedMessage == null) {
            return; // Do nothing if no message
        }
        messageListener.controlCommunication(parsedMessage);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLostConnection() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;

            try {
                Thread.sleep(800 * reconnectAttempts);
                attemptReconnection();
                return;
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                handleLostConnection(); // Retry if reconnection fails
                return;
            }
        }

        lostConnectionListener.onLostConnection(false);
    }

    private void attemptReconnection() throws IOException, IllegalArgumentException {
        connectToServer(lastServerIp, lastServerPort);
        authenticateUser();
        reconnectAttempts = 0;

        lostConnectionListener.onReconnectionSuccess();
        messageListenerThread = new Thread(this::listenForMessages);
        messageListenerThread.start();
    }
}
