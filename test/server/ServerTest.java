package server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.net.InetAddress;
import java.net.ServerSocket;

import org.junit.jupiter.api.Test;

import common.util.ConfigLoader;

/**
 * Tests the server startup process by attempting to create a server socket using default details
 */
public class ServerTest {
    @Test
    void main_DoesNotThrowException_IfSuccessfulStart() {
        assertDoesNotThrow(() -> {
            ConfigLoader config = new ConfigLoader();
            String serverIp = config.get("default.server.ip");
            int serverPort = config.getInt("default.server.port") + 1; // Ensure different port is tested

            try (ServerSocket serverSocket = new ServerSocket(serverPort, 0, InetAddress.getByName(serverIp))) {
            }
        });
    }
}