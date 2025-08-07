package client.model;

import javax.swing.Timer;

/**
 * Tracks user activity and toggles the user's status when the user is inactive after a set timeout, based on the config
 */
public class ActivityModel {
    private final int TIMEOUT = 30000; // milliseconds
    private final ConnectionManager connectionManager;
    private Timer timer;
    private boolean active = true;

    public ActivityModel(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(TIMEOUT, e -> {
            if (active) {
                active = false;
                connectionManager.toggleStatus();
            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    public void trackActivity() {
        if (!active) {
            active = true;
            connectionManager.toggleStatus();
        }
        startTimer();
    }

    public void shutdown() {
        if (timer != null) {
            timer.stop();
        }
    }
}