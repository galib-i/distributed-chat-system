package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import common.util.ConfigLoader;
import server.controller.ConnectionController;
import server.model.UserManager;

/**
 * Starts the server and listens for incoming connections
 * server ip and port are loaded from the config file
 */
public class Server {
    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader();
        UserManager userManager = new UserManager();
        ConnectionController connectionController = new ConnectionController(userManager);

        String serverIp = config.get("default.server.ip");
        int serverPort = config.getInt("default.server.port");

        System.out.println("STARTING SERVER %s:%d\n".formatted(serverIp, serverPort));

        try (ServerSocket serverSocket = new ServerSocket(serverPort, 0, InetAddress.getByName(serverIp))) {
            while (true) {
                connectionController.handleNewConnection(serverSocket.accept());
            }
        } catch (IOException e) {
            System.err.println("ERROR STARTING SERVER (%s)\n".formatted(e.getMessage()));
        }
    }
}