package de.jannek.birthday.reminder;

import de.jannek.birthday.reminder.gui.main.MainController;
import de.jannek.birthday.reminder.gui.main.MainGui;
import de.jannek.birthday.reminder.gui.settings.SettingsController;
import de.jannek.birthday.reminder.util.PropertiesUtils;
import de.jannek.birthday.reminder.util.Utils;
import javafx.application.Application;

import java.util.logging.Logger;

public class BirthdayReminder {

    public static final Logger LOGGER = Logger.getLogger(BirthdayReminder.class.getName());
    public static MainController mainController;
    public static SettingsController settingsController;

    public static void main(String... args) {

        long start = System.currentTimeMillis();
        System.out.println("Start...");
        System.out.println("loading logger...");
        Utils.loadLogger();
        LOGGER.info("loading properties...");
        PropertiesUtils.getInstance();
        LOGGER.info("loading gui...");
        new Thread(() -> Application.launch(MainGui.class)).start();
        LOGGER.info("done (" + (System.currentTimeMillis() - start) + " ms.)");
    }

}
