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
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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

        final JFileChooser chooser = new JFileChooser(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last"));
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
            JOptionPane.showMessageDialog(null, PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "error.internal", e.getLocalizedMessage()));
            e.printStackTrace();
        }


        chooser.setVisible(false);
    }

    @FXML
    protected void textAreaTyped(){
        System.out.println("textAreaTyped");
        updateListView(textFieldSearch.getText());
    }


    public void updateLanguage(String luanguage){
        PropertiesUtils.PropertyType.MESSAGE.copy(PropertiesUtils.PropertyType.valueOf("MESSAGE_"+luanguage.toUpperCase()));
        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
        logger.info("change the language to " + type.toString() + "...");

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
        listViewPast.getItems().clear();
        final SimpleDateFormat sdf = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.date.format"));
        final String format = PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.format");
        clearListView();
        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.PAST, filter)) {
            listViewPast.getItems().add(String.format(format, sdf.format(birthday.getDate()), birthday.getAge(), birthday.getName()));
        }
        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.TODAY, filter)) {
            listViewToday.getItems().add(String.format(format, sdf.format(birthday.getDate()), birthday.getAge(), birthday.getName()));
        }
        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.FUTURE, filter)) {
            listViewFuture.getItems().add(String.format(format, sdf.format(birthday.getDate()), birthday.getAge(true), birthday.getName()));
        }

        /*
        listViewPast.setCellFactory(vl -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            Birthday b = birthdayList.findBirthdayByName(cell.itemProperty().getName());

            Set<MenuItem> items = new HashSet<>();
            if(b.getMail() != null && !b.getMail().isEmpty()){
                MenuItem mail = new MenuItem();
                mail.textProperty().bind(Bindings.format("send mail to %s", cell.itemProperty()));
                mail.setOnAction(event -> {
                    String item = cell.getItem();
                    System.out.println(item);
                });
                items.add(mail);
            }

            if(b.getPhone() != null){
                MenuItem phone = new MenuItem();
                phone.textProperty().bind(Bindings.format("call %s", cell.itemProperty()));
                phone.setOnAction(event -> {
                    String item = cell.getItem();
                    System.out.println(item);
                });
                items.add(phone);
            }

            if(items.size() > 1){
                contextMenu.getItems().addAll(items);
                cell.textProperty().bind(cell.itemProperty());
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(contextMenu);
                    }
                });
            }

            return cell;
        });
        */
        MenuItem mi = new MenuItem("test");
        mi.textProperty().bind(Bindings.format("TEST %s", "TEST"));
        mi.setOnAction(event -> {
            System.out.println(mi.getText());
        });
        listViewPast.getItems().add(mi);
    }

    private void clearListView(){
        listViewPast.getItems().clear();
        listViewToday.getItems().clear();
        listViewFuture.getItems().clear();
    }

    Comparator<Birthday> birthdayComparator = new Comparator<Birthday>() {
        @Override
        public int compare(Birthday b1, Birthday b2) {
            return (b1.getDate().getTime() > b2.getDate().getTime()) ? 1 : 0;
        }
    };

}