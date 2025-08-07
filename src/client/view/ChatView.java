package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * Displays chat messages in a tabbed pane (Group chat and private chats)
 */
public class ChatView extends JPanel {
    private final JTabbedPane chatTabs = new JTabbedPane();
    private final HashMap<String, ChatPanel> chats = new HashMap<>();

    public ChatView() {
        setLayout(new BorderLayout());

        ChatPanel groupChatPanel = new ChatPanel();
        chats.put("Group", groupChatPanel);
        chatTabs.addTab("Group", groupChatPanel);

        add(chatTabs, BorderLayout.CENTER);
    }

    private static class ChatPanel extends JPanel {
        private final JTextArea chatArea;

        public ChatPanel() {
            setLayout(new BorderLayout());

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(chatArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    public void displayMessage(String chatName, String timestamp, String sender, String message) {
        ChatPanel chatPanel = chats.get(chatName);
        if (chatPanel == null || timestamp == null || sender == null || message == null) {
            return; // Do nothing if any missing parameter
        }
        chatPanel.chatArea.append("[%s] %s: %s\n".formatted(timestamp, sender, message));
    }

    public void displayReconnectedMessage() {
        ChatPanel groupChatPanel = chats.get("Group");
        groupChatPanel.chatArea.append("\n\nRECONNECTED\n\n");
    }

    public String getCurrentChatName() {
        return chatTabs.getTitleAt(chatTabs.getSelectedIndex());
    }

    public void openPrivateChat(String userId) {
        if (chats.containsKey(userId)) {
            return;
        }

        ChatPanel privateChatPanel = new ChatPanel();
        chats.put(userId, privateChatPanel);
        chatTabs.addTab(userId, privateChatPanel);
    }

    public void closePrivateChat(String userId) {
        ChatPanel privateChatPanel = chats.get(userId);
        if (privateChatPanel == null) {
            return;
        }

        int index = chatTabs.indexOfComponent(privateChatPanel);
        chats.remove(userId);
        chatTabs.removeTabAt(index);
    }
}
