package de.mcelements.birthday.reminder.gui.main;

import de.mcelements.birthday.reminder.Main;
import de.mcelements.birthday.reminder.util.Birthday;
import de.mcelements.birthday.reminder.util.BirthdayList;
import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
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
    protected void buttonSettings() {
        System.out.println("buttonSettings");
        new Utils.FXML("gui/settings/settingsGui.fxml");
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

    public void updateLanguage(){
        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
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


        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.TODAY, filter)) {
            listViewToday.getItems().add(birthday);
        }


        for (Birthday birthday : birthdayList.findBirthdays(BirthdayList.BirthdayType.FUTURE, filter, limitFuture)) {
            listViewFuture.getItems().add(birthday);
        }


        setContextMenuNew(listViewPast);
        setContextMenuNew(listViewToday);
        setContextMenuNew(listViewFuture);


        SimpleDateFormat labelSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.date.format"));
        labelPast.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.past", labelSDF.format(getDate(limitPast*-1))));
        labelToday.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.today", labelSDF.format(new Date())));
        labelFuture.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.future", labelSDF.format(getDate(limitFuture))));


    }

    private void clearListView(){
        listViewPast.getItems().clear();
        listViewToday.getItems().clear();
        listViewFuture.getItems().clear();
    }

    private Date getDate(int days){
        return new Date(System.currentTimeMillis()+(1000*60*60*24*days));
    }

    private void setContextMenuNew(ListView<Birthday> listView){
        final String fontName = PropertiesUtils.getInstance().getPropertyOrDefault(PropertiesUtils.PropertyType.SETTINGS, "gui.list.font.name", "Arial");
        String size = PropertiesUtils.getInstance().getPropertyOrDefault(PropertiesUtils.PropertyType.SETTINGS, "gui.list.font.size", "12");
        int i = 12;
        try {
            i = Integer.parseInt(size);
        }catch (Exception ex){}
        final int fontSize = i;

        Font font1;
        try{
            font1 = new Font(fontName, fontSize);
        }catch (Exception e){
            font1 = new Font(fontSize);
        }
        if(font1 == null)
            font1 = new Font(12);

        final Font font = font1;

        listView.setCellFactory(lv -> {
            ListCell<Birthday> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            cell.setFont(font);

            MenuItem mail = new MenuItem(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.mail"));
            mail.setOnAction((ActionEvent event) -> {
                Birthday birthday = cell.getItem();
                if(birthday.getMail() == null || birthday.getMail().isEmpty()){
                    JOptionPane.showMessageDialog(null, PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.mail.not.found"));
                } else {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(birthday.getMail()), null);
                    URI mailURI = URI.create(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "strings.format.mail", birthday.getMail(true)));
                    try {
                        Desktop.getDesktop().mail(mailURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            MenuItem phone = new MenuItem(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.phone"));
            phone.setOnAction((ActionEvent event) -> {
                Birthday birthday = cell.getItem();
                if(birthday.getPhone() == null){
                    JOptionPane.showMessageDialog(null, PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.phone.not.found"));
                } else {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(birthday.getPhone()), null);
                    JOptionPane.showMessageDialog(null, birthday.getPhone(true));
                }
            });

            contextMenu.getItems().addAll(mail, phone);

            StringBinding stringBinding = new StringBinding(){
                {
                    super.bind(cell.itemProperty().asString());
                }
                @Override
                protected String computeValue() {
                    if(cell.itemProperty().getValue()==null){
                        return "";
                    }
                    return cell.itemProperty().getValue().getListText();
                }
            };

            cell.textProperty().bind(stringBinding);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setVisible(false);
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });
    }

}