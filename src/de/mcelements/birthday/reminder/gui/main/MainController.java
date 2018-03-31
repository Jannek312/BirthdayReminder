package de.mcelements.birthday.reminder.gui.main;

import de.mcelements.birthday.reminder.Main;
import de.mcelements.birthday.reminder.util.Birthday;
import de.mcelements.birthday.reminder.util.BirthdayList;
import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    protected ListView listViewPast = new ListView<Birthday>();
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


    public void updateLanguage(String language){
        PropertiesUtils.PropertyType.MESSAGE.copy(PropertiesUtils.PropertyType.valueOf("MESSAGE_" + language.toUpperCase()));
        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
        logger.info("change the language to " + type.toString() + "...");
        PropertiesUtils.getInstance().setProperty(PropertiesUtils.PropertyType.SETTINGS, "language", language.toLowerCase());

        System.out.println(type.toString());

        SimpleDateFormat guiSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(type, "gui.title.date.format"));
        MainGui.stage.setTitle(PropertiesUtils.getInstance().getProperty(type, "gui.title", guiSDF.format(new Date())));

        SimpleDateFormat labelSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.date.format"));
        boolean ignoreLimit = checkBoxIgnoreLimit.isSelected();
        int limitPast = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.past")) : -1;
        int limitFuture = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.future")) : -1;
        labelPast.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.past", labelSDF.format(getDate(limitPast*-1))));
        labelToday.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.today", labelSDF.format(new Date())));
        labelFuture.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.future", labelSDF.format(getDate(limitFuture))));

        textFieldSearch.setPromptText(PropertiesUtils.getInstance().getProperty(type, "gui.text.search"));

        checkBoxIgnoreLimit.setText(PropertiesUtils.getInstance().getProperty(type, "gui.check.box.ignore.limit"));

        buttonSettings.setText(PropertiesUtils.getInstance().getProperty(type, "gui.button.settings"));
        buttonLoadFile.setText(PropertiesUtils.getInstance().getProperty(type, "gui.button.load.file"));

        updateListView();
    }

    public void updateList(BirthdayList birthdayList){
        this.birthdayList = birthdayList;
        updateListView(textFieldSearch.getText());
    }
    public void updateListView(){
        if(textFieldSearch == null || birthdayList == null) return;
        updateListView(textFieldSearch.getText());
    }

    public void updateListView(final String filter){
        boolean ignoreLimit = checkBoxIgnoreLimit.isSelected();
        int limitPast = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.past")) : -1;
        int limitFuture = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.future")) : -1;

        clearListView();


        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.PAST, filter, limitPast)) {
            listViewPast.getItems().add(birthday);

        }

        ContextMenu contextMenuPast = new ContextMenu();
        MenuItem itemPhone = new MenuItem(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.phone"));
        itemPhone.setOnAction(event -> {
            Birthday birthday = (Birthday) listViewPast.getSelectionModel().getSelectedItem();
            JOptionPane.showMessageDialog(null, birthday.getPhone(true));
        });

        MenuItem itemMail = new MenuItem(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.mail"));
        itemMail.setOnAction(event -> {
            Birthday birthday = (Birthday) listViewPast.getSelectionModel().getSelectedItem();
            if(birthday.getMail() != null){
                final URI uri = URI.create(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "strings.format.mail", birthday.getMail()));
                try {
                    Desktop.getDesktop().mail(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "TODO");//TODO message
            }
        });

        contextMenuPast.getItems().addAll(itemPhone, itemMail);
        listViewPast.setContextMenu(contextMenuPast);
        listViewPast.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            @Override
            public void handle(ContextMenuEvent event) {
                contextMenuPast.show(listViewPast, event.getScreenX(), event.getScreenY());
                event.consume();
            }

        });


        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.TODAY, filter)) {
            listViewToday.getItems().add(birthday.getListText());
        }


        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.FUTURE, filter, limitFuture)) {
            listViewFuture.getItems().add(birthday.getListText(true));
        }



        SimpleDateFormat labelSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.date.format"));
        labelPast.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.past", labelSDF.format(getDate(limitPast*-1))));
        labelToday.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.today", labelSDF.format(new Date())));
        labelFuture.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.future", labelSDF.format(getDate(limitFuture))));

        /*
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            Birthday b = birthdayList.findBirthdayByName(cell.itemProperty().toString());
            System.out.println(vl.toString());
            System.out.println(cell.getText());
            System.out.println(cell.toString());

            System.out.println(b + ": " + cell.itemProperty().toString());

            Set<MenuItem> items = new HashSet<>();
            if(b != null && b.getMail() != null && !b.getMail().isEmpty()){
                MenuItem mail = new MenuItem();
                mail.textProperty().bind(Bindings.format("send mail to %s", cell.itemProperty()));
                mail.setOnAction(event -> {
                    String item = cell.getItem();
                    System.out.println(item);
                });
                items.add(mail);
            }

            if(b != null && b.getPhone() != null){
                MenuItem phone = new MenuItem();
                phone.textProperty().bind(Bindings.format("call %s", cell.itemProperty()));
                phone.setOnAction(event -> {
                    String item = cell.getItem();
                    System.out.println(item);
                });
                items.add(phone);
            }


            MenuItem phone = new MenuItem();
            phone.textProperty().bind(Bindings.format("call %s", cell.itemProperty()));
            phone.setOnAction(event -> {
                String item = cell.getItem();
                System.out.println(item);
            });
            items.add(phone);


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
    }

    private void clearListView(){
        listViewPast.getItems().clear();
        listViewToday.getItems().clear();
        listViewFuture.getItems().clear();
    }

    private Date getDate(int days){
        return new Date(System.currentTimeMillis()+(1000*60*60*24*days));
    }

}