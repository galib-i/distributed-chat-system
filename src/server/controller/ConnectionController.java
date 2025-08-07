package server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.model.Message;
import common.util.MessageFormatter;
import server.model.User;
import server.model.UserManager;

/**
 * Controls the server side connection between the server and a client
 */
public class ConnectionController {
    private final UserManager userManager;
    private final MessageController messageController;

    public ConnectionController(UserManager userManager) {
        this.userManager = userManager;
        this.messageController = new MessageController(userManager);
    }

    /**
     * Processes the new connection from a client by creating a new thread to control the connection
     * 
     * @param socket Socket connection to the client
     */
    public void handleNewConnection(Socket socket) {
        new Thread(() -> controlConnection(socket)).start();
    }

    /**
     * Processes the connection (join/leave) between the server and client
     * 
     * @param socket Socket connection to the client
     */
    private void controlConnection(Socket socket) {
        String userId = null;
        boolean userAdded = false;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String joinRequest = reader.readLine();
            Message joinResponse = MessageFormatter.parse(joinRequest);

            if (joinResponse == null || joinResponse.getType() != Message.Type.USER_JOIN) {
                return;
            }

            userId = joinResponse.getSender();

            if (userManager.getUser(userId) != null) {
                Message rejectMessage = Message.rejectJoin(userId);
                writer.println(MessageFormatter.format(rejectMessage));
                return;
            }
            userAdded = true;
            String socketAddress = "%s:%d".formatted(socket.getInetAddress().getHostAddress(), socket.getPort());
            controlUserJoin(userId, socket, reader, writer, socketAddress);
            controlClientCommunication(userId, reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (userAdded) {
                controlDisconnection(userId, socket);
            }
        }
    }

    private void controlUserJoin(String userId, Socket socket, BufferedReader reader, PrintWriter writer,
            String socketAddress) {
        userManager.addUser(new User(userId, socketAddress, writer));
        messageController.controlUserJoin(userId);
    }

    /**
     * Processes the communication (different types of messages) between the server and client
     * 
     * @param reader BufferedReader to read messages from the client
     * @param userId Id of the user
     * @see MessageController
     */
    private void controlClientCommunication(String userId, BufferedReader reader) {
        try {
            String messageStr;
            while ((messageStr = reader.readLine()) != null) { // Constantly listen for messages from the client
                Message message = MessageFormatter.parse(messageStr);
                messageController.controlCommunication(userId, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void controlDisconnection(String userId, Socket socket) {
        try {
            boolean isCoordinator = userManager.getCoordinatorId().equals(userId);
            userManager.removeUser(userId);
            messageController.controlUserLeave(userId, isCoordinator);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
