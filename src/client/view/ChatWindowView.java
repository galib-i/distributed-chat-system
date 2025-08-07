package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The main chat window that displays the chat messages, user list and bottom panel
 */
public class ChatWindowView extends JFrame {
    private final ChatView chatView = new ChatView();
    private final UserListView userListView = new UserListView();
    private final JTextField messageField = new JTextField();
    private final JButton sendButton = new JButton("Send");
    private final JButton quitButton = new JButton("Quit");
    private final JLabel currentServerLabel = new JLabel();

    private String serverIp = null;
    private String serverPort = null;

    public ChatWindowView() {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));

        mainPanel.add(chatView, BorderLayout.CENTER);
        rootPanel.add(userListView, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        messageField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        rootPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new BorderLayout(5, 0));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        infoPanel.add(currentServerLabel, BorderLayout.WEST);
        infoPanel.add(quitButton, BorderLayout.EAST);
        rootPanel.add(infoPanel, BorderLayout.SOUTH);

        add(rootPanel);
        setSize(700, 400);
        setLocationRelativeTo(null); // center window

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        exitWindowAction(); // window closure mimics quit button
    }

    private void exitWindowAction() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitButton.doClick();
            }
        });
    }

    public void updateCurrentServerLabel(String serverIp, String serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        currentServerLabel.setText("Connected to %s:%s".formatted(serverIp, serverPort));
    }

    public void quitButtonAction(ActionListener listener) {
        quitButton.addActionListener(listener);
    }

    public void sendButtonAction(ActionListener listener) {
        sendButton.addActionListener(listener);
        messageField.addActionListener(listener); // Enter key mimics send button
    }

    public String getMessage() {
        String message = messageField.getText();
        messageField.setText("");
        if (message.isBlank()) {
            return null;
        }
        return message;
    }

    public ChatView getChatView() {
        return chatView;
    }

    public UserListView getUserListView() {
        return userListView;
    }

    public void disableForReconnectAttempt() {
        currentServerLabel.setText("Lost connection! Attempting to reconnect...");
        currentServerLabel.setForeground(Color.RED);

        toggleComponentsUse(false);
    }

    public void enableAfterReconnection() {
        currentServerLabel.setText("Connected to %s:%s".formatted(serverIp, serverPort));
        currentServerLabel.setForeground(Color.BLACK);

        toggleComponentsUse(true);
    }

    private void toggleComponentsUse(boolean enabled) {
        messageField.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        userListView.setEnabled(enabled);

        chatView.setEnabled(enabled);
    }

    public void showLostConnectionMessage() {
        JOptionPane.showMessageDialog(this, "Connection to server lost", "Server Disconnected",
                JOptionPane.ERROR_MESSAGE);
    }
}
