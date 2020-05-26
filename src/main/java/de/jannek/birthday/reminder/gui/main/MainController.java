package de.jannek.birthday.reminder.gui.main;

import de.jannek.birthday.reminder.BirthdayReminder;
import de.jannek.birthday.reminder.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class MainController {
    BirthdayList birthdayList = null;

    private final Logger logger = BirthdayReminder.LOGGER;

    public MainController() {
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
    protected void buttonLoadFile() {
        logger.info("button load file pressed");

        String path = PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last", false);
        if (path == null || path.isEmpty()) {
            try {
                path = BirthdayReminder.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            } catch (URISyntaxException e) {
            }
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
    protected void textAreaTyped() {
        System.out.println("textAreaTyped");
        updateListView(textFieldSearch.getText());
    }

    public void updateLanguage() {
        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
        SimpleDateFormat guiSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(type, "gui.title.date.format"));
        MainGui.stage.setTitle(PropertiesUtils.getInstance().getProperty(type, "gui.title", guiSDF.format(new Date())));

        SimpleDateFormat labelSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.date.format"));
        boolean ignoreLimit = checkBoxIgnoreLimit.isSelected();
        int limitPast = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.past")) : -1;
        int limitFuture = !ignoreLimit ? Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.future")) : -1;
        setListTitles(labelSDF, limitPast, limitFuture);

        textFieldSearch.setPromptText(PropertiesUtils.getInstance().getProperty(type, "gui.text.search"));

        checkBoxIgnoreLimit.setText(PropertiesUtils.getInstance().getProperty(type, "gui.check.box.ignore.limit"));

        buttonSettings.setText(PropertiesUtils.getInstance().getProperty(type, "gui.button.settings"));
        buttonLoadFile.setText(PropertiesUtils.getInstance().getProperty(type, "gui.button.load.file"));

        updateListView();
    }

    private void setListTitles(final SimpleDateFormat simpleDateFormat, final int limitPast, final int limitFuture) {
        labelPast.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.past" + (limitPast == -1 ? ".no" : "") + ".limit",
                simpleDateFormat.format(getDate(limitPast * -1))));

        labelToday.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.today", simpleDateFormat.format(new Date())));

        labelFuture.setText(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.future" + (limitFuture == -1 ? ".no" : "") + ".limit",
                simpleDateFormat.format(getDate(limitFuture))));
    }

    public void updateList(BirthdayList birthdayList) {
        this.birthdayList = birthdayList;
        updateListView(textFieldSearch.getText());
    }

    public void updateListView() {
        if (textFieldSearch == null || birthdayList == null) return;
        updateListView(textFieldSearch.getText());
    }

    public void updateListView(final String filter) {
        boolean ignoreLimit = checkBoxIgnoreLimit.isSelected();
        int limitPast = ignoreLimit ? -1 : Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.past"));
        int limitFuture = ignoreLimit ? -1 : Integer.parseInt(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "limit.future"));

        clearListView();

        listViewPast.getItems().addAll(birthdayList.findBirthdays(BirthdayList.BirthdayType.PAST, filter, limitPast));
        listViewToday.getItems().addAll(birthdayList.findBirthdays(BirthdayList.BirthdayType.TODAY, filter));
        listViewFuture.getItems().addAll(birthdayList.findBirthdays(BirthdayList.BirthdayType.FUTURE, filter, limitFuture));

        setContextMenuNew(listViewPast);
        setContextMenuNew(listViewToday);
        setContextMenuNew(listViewFuture);


        SimpleDateFormat labelSDF = new SimpleDateFormat(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.label.date.format"));
        setListTitles(labelSDF, limitPast, limitFuture);
    }

    private void clearListView() {
        listViewPast.getItems().clear();
        listViewToday.getItems().clear();
        listViewFuture.getItems().clear();
    }

    private Date getDate(int days) {
        return new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * days));
    }

    private void setContextMenuNew(ListView<Birthday> listView) {
        final String fontName = PropertiesUtils.getInstance().getPropertyOrDefault(PropertiesUtils.PropertyType.SETTINGS, "gui.list.font.name", "Arial");
        String size = PropertiesUtils.getInstance().getPropertyOrDefault(PropertiesUtils.PropertyType.SETTINGS, "gui.list.font.size", "12");
        int i = 12;
        try {
            i = Integer.parseInt(size);
        } catch (Exception ignored) {
        }
        final int fontSize = i;

        Font font;
        try {
            font = new Font(fontName, fontSize);
        } catch (Exception e) {
            font = new Font(fontSize);
        }

        final Font finalFont = font;
        listView.setCellFactory(studentListView -> new BirthdayListViewCell(finalFont));
    }

}