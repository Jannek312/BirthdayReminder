package de.mcelements.birthday.reminder.gui.main;

import de.mcelements.birthday.reminder.Main;
import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        stage.show();

        Main.mainController = loader.getController();
        Main.mainController.updateLanguage(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "language"));

        while (true) {
            try {
                Utils.loadFile(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last"));
                return;
            }catch (FileNotFoundException ex){//TODO FileNotFound / Used by another process!
                    if(JOptionPane.showConfirmDialog(null, PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "error.internal", ex.getMessage())) != 0){
                        return;
                    }
            }catch (Exception ex){
                JOptionPane.showMessageDialog(null, PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "error.internal", ex.getMessage()));
                ex.printStackTrace();
                System.exit(0);
            }
        }

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
