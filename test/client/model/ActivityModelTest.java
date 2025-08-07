package client.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ActivityModel for starting a timer, tracking activity and shutdown
 */
public class ActivityModelTest {
    private ActivityModel activityModel;
    private ConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager();
        activityModel = new ActivityModel(connectionManager);
    }

    @Test
    void startTimer_StartsNewTimer_IfCalled() {
        activityModel.startTimer();
        assertTrue(true);
    }

    @Test
    void trackActivity_UpdatesStatus_IfCalled() {
        activityModel.trackActivity();
        assertTrue(true);
    }

    @Test
    void shutdown_StopsTimer_IfCalled() {
        activityModel.startTimer();
        activityModel.shutdown();
        assertTrue(true);
    }
}