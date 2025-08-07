package common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ConfigLoader for loading configuration properties from config.properties
 */
public class ConfigLoaderTest {
    private ConfigLoader configLoader;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader();
    }

    @Test
    void get_ReturnsString_IfGetSavedProperty() {
        assertEquals("localhost", configLoader.get("default.server.ip"));
    }

    @Test
    void get_ReturnsNull_IfGetNotSavedProperty() {
        assertNull(configLoader.get("not.saved"));
    }

    @Test
    void getInt_ReturnsInt_IfGetSavedNumPropety() {
        assertEquals(1549, configLoader.getInt("default.server.port"));
    }

    @Test
    void getInt_ThrowsNumberFormatException_IfGetSavedNonNumProperty() {
        assertThrows(NumberFormatException.class, () -> configLoader.getInt("default.server.ip"));
    }
}