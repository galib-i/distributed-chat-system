package server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.PrintWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the User class by creating a user and checking its properties, PrintWriter is mocked as null as it is not used
 * in the tests
 */
public class UserTest {
    private User user;
    private final String USER_ID = "UserId1";
    private final String SOCKET_ADDRESS = "127.0.0.1:1549";
    private PrintWriter MOCK_WRITER = null;

    @BeforeEach
    void setUp() {
        user = new User(USER_ID, SOCKET_ADDRESS, MOCK_WRITER);
    }

    @Test
    void getUserId_ReturnsUserId_IfCalled() {
        assertEquals(USER_ID, user.getUserId());
    }

    @Test
    void getRole_ReturnsMemberRole_IfNotCoordinator() {
        assertEquals(User.Role.MEMBER, user.getRole());
    }

    @Test
    void promoteToCoordinator_ChangesRoleToCoordinator_IfNotCoordinator() {
        user.promoteToCoordinator();
        assertEquals(User.Role.COORDINATOR, user.getRole());
    }

    @Test
    void getStatus_ReturnsStatusActive_IfActive() {
        assertEquals(User.Status.ACTIVE, user.getStatus());
    }

    @Test
    void toggleStatus_ChangesToInactive_IfActiveStatusToggled() {
        user.toggleStatus();
        assertEquals(User.Status.INACTIVE, user.getStatus());
    }

    @Test
    void toggleStatus_ChangesToActive_IfInactiveUserToggled() {
        user.toggleStatus(); // Inactive
        user.toggleStatus(); // Active
        assertEquals(User.Status.ACTIVE, user.getStatus());
    }

    @Test
    void getSocketAddress_ReturnsSocketAddress_IfCalled() {
        assertEquals(SOCKET_ADDRESS, user.getSocketAddress());
    }

    @Test
    void getWriter_ReturnsWriter_IfCalled() {
        assertSame(MOCK_WRITER, user.getWriter());
    }
}
