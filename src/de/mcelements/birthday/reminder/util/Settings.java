package de.mcelements.birthday.reminder.util;

public class Settings {

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null)
            instance = new Settings();
        return instance;
    }

    private String pathLast;

    private int limitPast;
    private int limitFuture;

    private Settings() {
        PropertiesUtils propertiesUtils = PropertiesUtils.getInstance();
        pathLast = propertiesUtils.getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last");
        limitPast = Integer.parseInt(propertiesUtils.getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.past"));
        limitFuture = Integer.parseInt(propertiesUtils.getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.future"));
    }

    public String getPathLast() {
        return pathLast;
    }

    public int getLimitPast() {
        return limitPast;
    }

    public int getLimitFuture() {
        return limitFuture;
    }


    public void reload() {
        instance = new Settings();
    }
}
