package de.mcelements.birthday.reminder.util;

import de.mcelements.birthday.reminder.Main;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Utils {
    static Logger logger = Main.LOGGER;

    public static void loadLogger(){
        Main.LOGGER.setUseParentHandlers(false);
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
        Main.LOGGER.addHandler(handler);
    }

    public static void loadFile(String fileName) throws Exception{
        if(fileName == null || fileName.isEmpty()){
            logger.warning("loadFile with empty or null file name");
            return;
        }
        loadFile(new File(fileName));
    }

    public static void loadFile(File file) throws Exception{
        PropertiesUtils.getInstance().setProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last", file.getPath());

        final BirthdayList birthdayList = new BirthdayList();

        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();

        sheet.forEach(row -> {
            Birthday birthday = new Birthday(
                    dataFormatter.formatCellValue(row.getCell(0)),
                    dataFormatter.formatCellValue(row.getCell(1)),
                    dataFormatter.formatCellValue(row.getCell(2)));
            birthdayList.add(birthday);


            System.out.println(birthday.toString());
        });

        workbook.close();
        Main.mainController.updateList(birthdayList);
    }

    public static void test(){
        Main.mainController.updateLanguage((new Random().nextInt(2) == 1) ? "de" : "en");
    }



}
