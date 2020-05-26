package de.mcelements.birthday.reminder.gui.main;

import de.mcelements.birthday.reminder.BirthdayReminder;
import de.mcelements.birthday.reminder.util.PropertiesUtils;
import de.mcelements.birthday.reminder.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainGui extends Application {

    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setMinHeight(400.0);
        stage.setMinWidth(600.0);

        stage.setScene(scene);
        stage.getIcons().add(new Image(BirthdayReminder.class.getResourceAsStream("/favicon.png")));

        /*
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.setMaximized(true);
            }
        });
        */

        stage.show();

        BirthdayReminder.mainController = loader.getController();
        Utils.updateLanguage(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "language"));

        if (!PropertiesUtils.getInstance().containsProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last")) {
            return;
        }

        Utils.loadFile(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.SETTINGS, "path.last"));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
