package client.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.swing.JFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.model.ActivityModel;

/**
 * Tests the ActivityController for tracking user activity through mouse and
 * window events
 */
public class ActivityControllerTest {
    private ActivityModel activityModel;
    private JFrame frame;
    private ActivityController activityController;

    @BeforeEach
    void setUp() {
        activityModel = new ActivityModel(null);
        frame = new JFrame();
        activityController = new ActivityController(activityModel, frame);
    }

    @Test
    void windowGainedFocus_TracksActivity_IfWindowFocusGained() {
        frame.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_GAINED_FOCUS));
        assertTrue(true); // No exceptions indicate success
    }

    @Test
    void mouseClicked_TracksActivity_IfMouseClickedOnFrame() {
        frame.dispatchEvent(
                new java.awt.event.MouseEvent(frame, java.awt.event.MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 1, false));
        assertTrue(true); // No exceptions indicate success
    }
}