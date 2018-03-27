package de.mcelements.birthday.reminder.gui.main;

import de.mcelements.birthday.reminder.Main;
import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.net.URL;

public class MainGui extends Application {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception{
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setMinHeight(400.0);
        stage.setMinWidth(600.0);

        stage.setScene(scene);
        System.out.println(Main.class.getResourceAsStream("/favicon.png"));
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/favicon.png")));

        stage.show();

        Main.mainController = loader.getController();
        Main.mainController.updateLanguage(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "language"));

        if(!PropertiesUtils.getInstance().containsProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last")){
            return;
        }
        JOptionPane.showMessageDialog(null, PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last"));
        Utils.loadFile(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last"));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
