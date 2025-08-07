package client.model;

/**
 * Interface for when the server is disconnected, but the clients are still running
 */
public interface LostConnectionListener {
    void onLostConnection(boolean attemptReconnection);

    void onReconnectionSuccess();
}