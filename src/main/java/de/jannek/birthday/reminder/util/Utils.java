package de.jannek.birthday.reminder.util;

import de.jannek.birthday.reminder.BirthdayReminder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Utils {
    static Logger logger = BirthdayReminder.LOGGER;

    public static void loadLogger() {
        BirthdayReminder.LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%1$tF %1$tT] [%2$-7s] %3$s %n",
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage()
                );
            }
        });

        BirthdayReminder.LOGGER.addHandler(handler);
    }

    public static void loadFile(String fileName) throws Exception {
        if (fileName == null || fileName.isEmpty()) {
            logger.warning("loadFile with empty or null file name");
            return;
        }
        try {
            loadFile(new File(fileName));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void loadFile(File file) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
        PropertiesUtils.getInstance().setProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last", file.getPath());

        final BirthdayList birthdayList = new BirthdayList();
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        sheet.forEach(row -> {
            final String date = dataFormatter.formatCellValue(row.getCell(0));
            final String name = dataFormatter.formatCellValue(row.getCell(1));
            Birthday birthday = new Birthday(
                    date,
                    name,
                    dataFormatter.formatCellValue(row.getCell(2)));
            if (date != null && !date.isEmpty() && name != null && !name.isEmpty()) {
                birthdayList.add(birthday);
                System.out.println("ADD: " + birthday.toString());
            } else {
                System.out.println("ERROR: ");
            }
        });

        workbook.close();
        BirthdayReminder.mainController.updateList(birthdayList);
    }

    public static class FXML {
        public FXML(final String filename) {
            Stage stage = new Stage();
            FXMLLoader loader;
            Parent parent;
            try {
                loader = new FXMLLoader(BirthdayReminder.class.getResource(filename));
                parent = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            if (filename.contains("settingsGui.fxml"))
                BirthdayReminder.settingsController = loader.getController();
        }
    }

    public static void updateLanguage(final String language) {
        PropertiesUtils.PropertyType lang = null;
        for (PropertiesUtils.PropertyType type : PropertiesUtils.PropertyType.values()) {
            if (type.name().equals(language.toUpperCase()) || type.name().equals("MESSAGE_" + language.toUpperCase()) ||
                    type.getKey().equals(language) || type.getDisplayName().equals(language)) {
                lang = type;
                System.out.println("set to " + type.name());
            }
        }
        if (lang == null) {
            logger.warning("PropertyType " + language + " not found!");
            return;
        }
        PropertiesUtils.PropertyType.MESSAGE.copy(lang);

        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
        logger.info("change the language to " + type.toString() + "...");
        PropertiesUtils.getInstance().setProperty(PropertiesUtils.PropertyType.SETTINGS, "language", language.toLowerCase());

        System.out.println(type.toString());

        BirthdayReminder.mainController.updateLanguage();
        if (BirthdayReminder.settingsController != null)
            BirthdayReminder.settingsController.updateLanguage();
    }


}
