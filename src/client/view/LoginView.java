package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.util.ConfigLoader;

/**
 * The login window that prompts user for id and server details (placeholder values match hosted server details)
 */
public class LoginView extends JFrame {
    private final JTextField userIdField = new JTextField();
    private final JTextField serverIpField = new JTextField();
    private final JTextField serverPortField = new JTextField();
    private final JButton connectButton = new JButton("Connect");

    public LoginView() {
        ConfigLoader config = new ConfigLoader();
        String defaultIp = config.get("default.server.ip");
        String defaultPort = String.valueOf(config.getInt("default.server.port"));

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(64, 32, 64, 32));
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        JPanel userIdPanel = createLabelledField("User ID:", "", userIdField);
        mainPanel.add(userIdPanel, BorderLayout.NORTH);

        JPanel serverDetailsPanel = new JPanel(new BorderLayout(5, 5));
        serverDetailsPanel.setBorder(BorderFactory.createTitledBorder("Server"));

        JPanel serverIpPanel = createLabelledField("IP:", defaultIp, serverIpField);
        JPanel serverPortPanel = createLabelledField("Port:", defaultPort, serverPortField);
        serverDetailsPanel.add(serverIpPanel, BorderLayout.NORTH);
        serverDetailsPanel.add(serverPortPanel, BorderLayout.CENTER);
        mainPanel.add(serverDetailsPanel, BorderLayout.CENTER);

        mainPanel.add(connectButton, BorderLayout.SOUTH);

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        add(rootPanel);
        getRootPane().setDefaultButton(connectButton);
        pack();

        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createLabelledField(String labelText, String placeholder, JTextField field) {
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        field.setColumns(15);
        field.setText(placeholder);

        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        label.setPreferredSize(new Dimension(45, 0));

        JPanel labelledField = new JPanel(new BorderLayout(5, 5));
        labelledField.add(label, BorderLayout.WEST);
        labelledField.add(field, BorderLayout.CENTER);

        return labelledField;
    }

    /**
     * Access all input fields all at once for easy validation
     */
    public record ConnectionDetails(String userId, String serverIp, String serverPort) {
    }

    public ConnectionDetails getConnectionDetails() {
        return new ConnectionDetails(userIdField.getText(), serverIpField.getText(), serverPortField.getText());
    }

    public void connectButtonAction(ActionListener listener) {
        connectButton.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
