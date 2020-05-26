package de.jannek.birthday.reminder.util;

import de.jannek.birthday.reminder.BirthdayReminder;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesUtils {

    private static final Logger LOGGER = BirthdayReminder.LOGGER;

    private static PropertiesUtils instance;

    public static PropertiesUtils getInstance() {
        if (instance == null)
            instance = new PropertiesUtils();
        return instance;
    }

    private PropertiesUtils() {
        for (PropertyType type : PropertyType.values()) {
            LOGGER.info("loading " + type.name() + " (" + type.getPath() + ")");
            if (type.isInternal() == null) continue;
            if (type.isInternal()) {
                try (InputStream is = PropertiesUtils.class.getResourceAsStream(type.getPath())) {
                    type.getProperties().load(new InputStreamReader(is, "UTF-8"));
                } catch (Exception e) {
                    LOGGER.warning("an error occurred while trying to load " + type.getPath());
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                File file = new File(type.getPath());
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        LOGGER.warning("an error occurred while trying to load " + type.getPath());
                        e.printStackTrace();
                        System.exit(1);
                    }
                }

                try (FileReader reader = new FileReader(type.getPath())) {
                    type.getProperties().load(reader);
                } catch (IOException e) {
                    LOGGER.warning("an error occurred while trying to load " + type.getPath());
                    e.printStackTrace();
                    System.exit(1);
                }

                if (type.getCopy() != null) {
                    LOGGER.info("copying properties from " + type.getCopy().getPath() + " to " + type.getPath() + "...");
                    type.getCopy().getProperties().forEach((key, value) -> {
                        System.out.println(key + ": " + value);
                        if (!type.getProperties().containsKey(key))
                            type.getProperties().put(key, value);
                    });
                }
                Runtime.getRuntime().addShutdownHook(new Thread(type::save));
            }
        }
    }

    public boolean containsProperty(PropertyType type, String key) {
        return type.getProperties().containsKey(key);
    }

    public String getProperty(PropertyType propertyType, String key, String... args) {
        return getPropertyOrDefault(propertyType, key, "KEY", args);
    }

    public String getProperty(PropertyType propertyType, String key, boolean returnNull, String... args) {
        String s = (returnNull) ? null : "KEY";
        return getPropertyOrDefault(propertyType, key, s, args);
    }

    public String getPropertyOrDefault(PropertyType propertyType, String key, String defaultValue, String... args) {
        Properties properties = propertyType.getProperties();
        if (!properties.containsKey(key)) {
            LOGGER.warning("key " + key + " not found in " + propertyType.getPath());
            return defaultValue.equals("KEY") ? key : defaultValue;
        }
        String value = properties.getProperty(key);
        if (args.length != 0)
            try {
                value = String.format(value, args);
            } catch (Exception ex) {
                LOGGER.warning("an error occurred while trying to format \n" + value + "\n with " + Arrays.toString(args) + " (key: " + key + ", file: " + propertyType.getPath() + ")");
                ex.printStackTrace();
            }
        return value;
    }

    public void setProperty(PropertyType propertyType, String key, String value) {
        propertyType.getProperties().setProperty(key, value);
    }

    public enum PropertyType {
        CONFIG("cfg", "Config", "/config.properties", true),
        MESSAGE("msg", "Message"),
        MESSAGE_DE("msg_de", "Deutsch", "/de_message.properties", true),
        MESSAGE_EN("msg_en", "English", "/en_message.properties", true),
        SETTINGS_DEFAULT("set_def", "Default Settings", "/settings.properties", true),
        SETTINGS("set", "Settings", "settings.properties", false, PropertyType.SETTINGS_DEFAULT);

        final private String key;
        final private String displayName;
        private Properties properties = new Properties();
        private final String path;
        private final Boolean internal;
        private final PropertyType copy;

        PropertyType(String key, String displayName) {
            this.key = key;
            this.displayName = displayName;
            this.path = null;
            this.internal = null;
            this.copy = null;
        }

        PropertyType(String key, String displayName, String path, boolean internal) {
            this.key = key;
            this.displayName = displayName;
            this.path = path;
            this.internal = internal;
            this.copy = null;
        }

        PropertyType(String key, String displayName, String path, boolean internal, PropertyType copy) {
            this.key = key;
            this.displayName = displayName;
            this.path = path;
            this.internal = internal;
            this.copy = copy;
        }

        public String getKey() {
            return key;
        }

        public String getDisplayName() {
            return displayName;
        }

        private Properties getProperties() {
            return properties;
        }

        private String getPath() {
            return path;
        }

        private Boolean isInternal() {
            return internal;
        }

        private PropertyType getCopy() {
            return copy;
        }

        public void copy(PropertyType type) {
            properties = type.getProperties();
            LOGGER.info("copying properties from " + type + " to " + this + " (" + properties.keySet().size() + ")");
        }

        private void save() {
            if (isInternal()) return;
            LOGGER.info("saving properties to " + getPath() + "...");
            File file = new File(getPath());
            if (!file.exists())
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            try (OutputStream os = new FileOutputStream(file)) {
                getProperties().store(os, new Date().toString());
            } catch (Exception ex) {
                LOGGER.warning("An error occurred while trying to save " + getPath());
                ex.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "key: " + key + ", properties: " + properties.keySet().size();
        }
    }
}