package common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration properties from a file to be used by both client and server
 */
public class ConfigLoader {
    private static final String CONFIG_FILE = "config.properties";
    private final Properties properties = new Properties();

    public ConfigLoader() {
        try {
            InputStream input = new FileInputStream(CONFIG_FILE);
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading config file:\n%s".formatted(e.getMessage()));
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}