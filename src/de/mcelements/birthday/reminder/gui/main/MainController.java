package de.mcelements.birthday.reminder.gui.main;

import de.mcelements.birthday.reminder.Main;
import de.mcelements.birthday.reminder.util.Birthday;
import de.mcelements.birthday.reminder.util.BirthdayList;
import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class MainController {
    BirthdayList birthdayList = null;

    Logger logger = Main.LOGGER;

    public MainController(){
        System.out.println("loading MainController...");
    }


    @FXML
    protected Label labelPast;
    @FXML
    protected Label labelToday;
    @FXML
    protected Label labelFuture;

    @FXML
    protected ListView listViewPast;
    @FXML
    protected ListView listViewToday;
    @FXML
    protected ListView listViewFuture;

    @FXML
    protected Button buttonSettings;
    @FXML
    protected Button buttonLoadFile;

    @FXML
    protected CheckBox checkBoxIgnoreLimit;
    @FXML
    protected TextField textFieldSearch;

    @FXML
    protected void buttonSettings(){
        System.out.println("buttonSettings");
        Utils.test();
    }

    @FXML
    protected void buttonLoadFile(){
        logger.info("button load file pressed");

        String path = PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last", false);
        if(path == null || path.isEmpty()) {
            try {
                path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            } catch (URISyntaxException e) {}
        }


        final JFileChooser chooser = new JFileChooser(path);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "file.type.name"), "xls", "xlsx"));//TODO description
        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            //TODO error?
            chooser.setVisible(false);
            return;
        }

        try {
            Utils.loadFile(chooser.getSelectedFile());
        } catch (Exception e) {
            e.printStackTrace();
        }


        chooser.setVisible(false);
    }

    @FXML
    protected void textAreaTyped(){
        System.out.println("textAreaTyped");
        updateListView(textFieldSearch.getText());
    }


    public void updateLanguage(String language){
        PropertiesUtils.PropertyType.MESSAGE.copy(PropertiesUtils.PropertyType.valueOf("MESSAGE_" + language.toUpperCase()));
        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
        logger.info("change the language to " + type.toString() + "...");
        PropertiesUtils.getInstance().setProperty(PropertiesUtils.PropertyType.SETTINGS, "language", language.toLowerCase());

        System.out.println(type.toString());

        SimpleDateFormat guiSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(type, "gui.title.date.format"));
        MainGui.stage.setTitle(PropertiesUtils.getInstance().getProperty(type, "gui.title", guiSDF.format(new Date())));

        SimpleDateFormat labelSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(type, "gui.label.date.format"));
        labelPast.setText(PropertiesUtils.getInstance().getProperty(type, "gui.label.past", labelSDF.format(new Date())));
        labelToday.setText(PropertiesUtils.getInstance().getProperty(type, "gui.label.today", labelSDF.format(new Date())));
        labelFuture.setText(PropertiesUtils.getInstance().getProperty(type, "gui.label.future", labelSDF.format(new Date())));

        textFieldSearch.setPromptText(PropertiesUtils.getInstance().getProperty(type, "gui.text.search"));

        checkBoxIgnoreLimit.setText(PropertiesUtils.getInstance().getProperty(type, "gui.check.box.ignore.limit"));

        buttonSettings.setText(PropertiesUtils.getInstance().getProperty(type, "gui.button.settings"));
        buttonLoadFile.setText(PropertiesUtils.getInstance().getProperty(type, "gui.button.load.file"));
    }

    public void updateList(BirthdayList birthdayList){
        this.birthdayList = birthdayList;
        updateListView(textFieldSearch.getText());
    }
    public void updateListView(){
        updateListView("");
    }

    public void updateListView(final String filter){
        boolean ignoreLimit = checkBoxIgnoreLimit.isSelected();
        int limitPast = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.past")) : -1;
        int limitFuture = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.future")) : -1;

        listViewPast.getItems().clear();
        final SimpleDateFormat sdf = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.date.format"));
        final String format = PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.format");
        clearListView();
        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.PAST, filter, limitPast)) {
            listViewPast.getItems().add(String.format(format, sdf.format(birthday.getDate()), birthday.getAge(), birthday.getName()));
        }
        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.TODAY, filter)) {
            listViewToday.getItems().add(String.format(format, sdf.format(birthday.getDate()), birthday.getAge(), birthday.getName()));
        }
        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.FUTURE, filter, limitFuture)) {
            listViewFuture.getItems().add(String.format(format, sdf.format(birthday.getDate()), birthday.getAge(true), birthday.getName()));
        }

        listViewPast.setCellFactory(vl -> {
            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();


            MenuItem mail = new MenuItem();
            mail.textProperty().bind(Bindings.format("send mail to %s", cell.itemProperty()));
            mail.setOnAction(event -> {
                String item = cell.getItem();
                System.out.println(item);
            });
            MenuItem phone = new MenuItem();
            phone.textProperty().bind(Bindings.format("call %s", cell.itemProperty()));
            phone.setOnAction(event -> {
                String item = cell.getItem();
                System.out.println(item);
            });

            contextMenu.getItems().addAll(mail, phone);
            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell;
        });

    }

    private void clearListView(){
        listViewPast.getItems().clear();
        listViewToday.getItems().clear();
        listViewFuture.getItems().clear();
    }

}