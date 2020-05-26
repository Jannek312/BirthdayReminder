package de.mcelements.birthday.reminder;

import de.mcelements.birthday.reminder.util.Birthday;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ListViewContextMenuExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        ListView<Birthday> listView = new ListView<>();
        listView.getItems().addAll(
                new Birthday("13.06.1999", "Jannek Behrens", "+4917662272188"),
                new Birthday("31.03.1929", "Hans Sommer", "hans.sommer@gmail.com")

        );

        listView.setCellFactory(lv -> {

            ListCell<Birthday> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();


            System.out.println("Item: " + cell);


            MenuItem editItem = new MenuItem();
            editItem.textProperty().bind(Bindings.format("Edit \"%s\"", cell.itemProperty()));
            editItem.setOnAction(event -> {
                Birthday b = cell.getItem();
                System.out.println(b.getMail());
                // code to edit item...
            });
            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
            deleteItem.setOnAction(event -> listView.getItems().remove(cell.getItem()));
            contextMenu.getItems().addAll(editItem, deleteItem);

            cell.textProperty().bind(cell.itemProperty().asString());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            System.out.println(cell.getItem());

            return cell;
        });

        BorderPane root = new BorderPane(listView);
        primaryStage.setScene(new Scene(root, 250, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}