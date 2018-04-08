package de.mcelements.birthday.reminder.gui.settings;

import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsController {

    public SettingsController(){


    }

    @FXML
    public void initialize(){
        System.out.println("initialize SettingsController...");
        updateLanguage();

        textLimitPast.setText("5");
        textLimitFuture.setText("5");
        textDailyStart.setText("06:00");

        menuLanguage.getItems().clear();
        for (PropertiesUtils.PropertyType type : PropertiesUtils.PropertyType.values()) {
            if(!type.name().startsWith("MESSAGE_")) continue;
            MenuItem item = new MenuItem(type.getDisplayName());
            item.setOnAction(event -> Utils.updateLanguage(type.name()));
            menuLanguage.getItems().addAll(item);
        }

        textFontName.setText("Arial");
        textFontSize.setText("12");

        textLimitPast.setDisable(true);
        textLimitFuture.setDisable(true);
        textDailyStart.setDisable(true);
        textFontName.setDisable(true);
        textFontSize.setDisable(true);

    }


    @FXML
    Label labelLimitPast;
    @FXML
    Label labelLimitFuture;
    @FXML
    Label labelDailyStart;
    @FXML
    Label labelLanguage;
    @FXML
    Label labelFontSize;
    @FXML
    Label labelFontName;


    @FXML
    TextField textLimitPast;
    @FXML
    TextField textLimitFuture;
    @FXML
    TextField textDailyStart;
    @FXML
    MenuButton menuLanguage;
    @FXML
    TextField textFontSize;
    @FXML
    TextField textFontName;
    @FXML
    Button buttonApply;
    @FXML
    Button buttonCancel;


    @FXML
    protected void onTextLimitPast(){
        System.out.println("onSliderLimitPast");
    }

    @FXML
    protected void onTextLimitFuture(){
        System.out.println("onSliderLimitFuture");

    }

    @FXML
    protected void onTextDailyStart(){
        System.out.println("onTextDailyStart");

    }

    @FXML
    protected void onMenuLanguage(){
        System.out.println("onMenuLanguage");

    }

    @FXML
    protected void onTextFontSize(){
        System.out.println("onTextFontSize");

    }

    @FXML
    protected void onTextFontName(){
        System.out.println("onTextFontName");

    }

    @FXML
    protected void onButtonApply(){
        System.out.println("onButtonApply");

    }

    @FXML
    protected void onButtonCancel(){
        System.out.println("onButtonCancel");

    }



    public void updateLanguage(){
        PropertiesUtils.PropertyType type = PropertiesUtils.PropertyType.MESSAGE;
        PropertiesUtils propertiesUtils = PropertiesUtils.getInstance();
        labelLimitPast.setText(propertiesUtils.getProperty(type, "gui.settings.label.limit.past"));
        labelLimitFuture.setText(propertiesUtils.getProperty(type, "gui.settings.label.limit.future"));
        labelDailyStart.setText(propertiesUtils.getProperty(type, "gui.settings.label.daily.start"));
        labelLanguage.setText(propertiesUtils.getProperty(type, "gui.settings.label.language"));
        labelFontSize.setText(propertiesUtils.getProperty(type, "gui.settings.label.font.size"));
        labelFontName.setText(propertiesUtils.getProperty(type, "gui.settings.label.font.name"));
        buttonApply.setText(propertiesUtils.getProperty(type, "gui.settings.button.apply"));
        buttonCancel.setText(propertiesUtils.getProperty(type, "gui.settings.button.cancel"));
        menuLanguage.setText(propertiesUtils.getProperty(type, "language.key"));
    }
}