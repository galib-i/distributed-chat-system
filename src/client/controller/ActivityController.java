package client.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

import client.model.ActivityModel;

/**
 * Connects the activity model to the view (JFrame) and starts a timer
 */
public class ActivityController {
    private final ActivityModel model;

    public ActivityController(ActivityModel model, JFrame frame) {
        this.model = model;

        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                model.trackActivity();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
            }
        });

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.trackActivity();
            }
        });

        model.startTimer();
    }
}