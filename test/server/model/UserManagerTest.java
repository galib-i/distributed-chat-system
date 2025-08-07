package server.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the UserManager class by adding and removing users, checking their details.
 * Uses PrintWriter with StringWriter to mock I/O operations without real sockets
 */
public class UserManagerTest {
    private UserManager userManager;
    private final String USER_ID_1 = "User1";
    private final String USER_ID_2 = "User2";
    private final String SOCKET_ADDRESS = "127.0.0.1:1549";
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        StringWriter stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter, true);
    }

    private User createUser(String userId) {
        User user = new User(userId, SOCKET_ADDRESS, printWriter);
        userManager.addUser(user);
        return user;
    }

    private void createCoordinatorAndMember() {
        createUser(USER_ID_1); // First user becomes coordinator
        createUser(USER_ID_2); // Member
    }

    @Test
    void addUser_StoresUser_IfUserAdded() {
        User user = createUser(USER_ID_1);
        assertEquals(user, userManager.getUser(USER_ID_1));
    }

    @Test
    void addUser_PromotesToCoordinator_IfAddedFirstUser() {
        User user = createUser(USER_ID_1);

        assertEquals(USER_ID_1, userManager.getCoordinatorId());
        assertEquals(User.Role.COORDINATOR, user.getRole());
    }

    @Test
    void addUser_RemainsMember_IfNotFirstUser() {
        createCoordinatorAndMember();

        assertEquals(USER_ID_1, userManager.getCoordinatorId());
        assertEquals(User.Role.MEMBER, userManager.getUser(USER_ID_2).getRole());
    }

    @Test
    void removeUser_AssignsNewCoordinator_IfRemovedUserIsCoordinator() {
        createCoordinatorAndMember();
        userManager.removeUser(USER_ID_1);

        assertEquals(USER_ID_2, userManager.getCoordinatorId());
        assertEquals(User.Role.COORDINATOR, userManager.getUser(USER_ID_2).getRole());
    }

    @Test
    void removeUser_SetsCoordinatorToNull_IfNoUsers() {
        createUser(USER_ID_1);
        userManager.removeUser(USER_ID_1);

        assertNull(userManager.getCoordinatorId());
    }

    @Test
    void getUser_ReturnsNull_IfUserDoesNotExist() {
        assertNull(userManager.getUser(USER_ID_1));
    }

    @Test
    void getUserDetails_ReturnsHalfDetails_IfAllDetailsIsFalse() {
        createUser(USER_ID_1);
        Map<String, String> details = userManager.getUserDetails(USER_ID_1, false);

        assertEquals(2, details.size()); // Do not include userId and socketAddress
        assertEquals("COORDINATOR", details.get("role"));
        assertEquals("ACTIVE", details.get("status"));
    }

    @Test
    void getUserDetails_ReturnsAllDetails_IfAllDetailsIsTrue() {
        createUser(USER_ID_1);
        Map<String, String> details = userManager.getUserDetails(USER_ID_1, true);

        assertEquals(4, details.size());
        assertEquals(USER_ID_1, details.get("userId"));
        assertEquals("COORDINATOR", details.get("role"));
        assertEquals("ACTIVE", details.get("status"));
        assertEquals(SOCKET_ADDRESS, details.get("socketAddress"));
    }

    @Test
    void getUserDetails_ReturnsEmptyMap_IfUserDoesNotExist() {
        Map<String, String> details = userManager.getUserDetails("User99", true);

        assertTrue(details.isEmpty());
    }

    @Test
    void getAllUserDetails_ReturnsNestedMapOfAllUserDetails_IfAddedUsers() {
        createCoordinatorAndMember();
        Map<String, Map<String, String>> allDetails = userManager.getAllUserDetails();

        assertEquals(2, allDetails.size());
        assertTrue(allDetails.containsKey(USER_ID_1));
        assertTrue(allDetails.containsKey(USER_ID_2));
        assertEquals("COORDINATOR", allDetails.get(USER_ID_1).get("role"));
        assertEquals("MEMBER", allDetails.get(USER_ID_2).get("role"));
    }

    @Test
    void toggleUserStatus_ChangesUserStatus_IfUserExists() {
        User user = createUser(USER_ID_1);
        userManager.toggleUserStatus(USER_ID_1);
        assertEquals(User.Status.INACTIVE, user.getStatus());

        userManager.toggleUserStatus(USER_ID_1);
        assertEquals(User.Status.ACTIVE, user.getStatus());
    }
}
