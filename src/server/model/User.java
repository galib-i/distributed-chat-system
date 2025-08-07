package server.model;

import java.io.PrintWriter;

/**
 * Holds details about a connected user
 */
public class User {
    public enum Role {
        MEMBER, COORDINATOR
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    private final String userId;
    private Role role;
    private Status status;
    private final String socketAddress;
    private PrintWriter writer;

    public User(String userId, String socketAddress, PrintWriter writer) {
        this.userId = userId;
        this.role = Role.MEMBER;
        this.status = Status.ACTIVE;
        this.socketAddress = socketAddress;
        this.writer = writer;

    }

    public String getUserId() {
        return userId;
    }

    public Role getRole() {
        return role;
    }

    public void promoteToCoordinator() {
        this.role = Role.COORDINATOR;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Switches user status between active and inactive
     */
    public void toggleStatus() {
        if (status == Status.ACTIVE) {
            status = Status.INACTIVE;
        } else {
            status = Status.ACTIVE;
        }
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public PrintWriter getWriter() {
        return writer;
    }
}
