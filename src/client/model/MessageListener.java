package client.model;

import common.model.Message;

/**
 * Interface used to listen for messages from the server to update the client,
 * processing based on the message type connected through the ConnectionManager,
 *  implemented by MessageController
 */
@FunctionalInterface
public interface MessageListener {
    void controlCommunication(Message message);
}