package de.mcelements.birthday.reminder.util;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;

public class BirthdayListViewCell extends ListCell<Birthday> {

    private final Font font;

    public BirthdayListViewCell(Font font) {
        this.font = font;
    }

    @Override
    protected void updateItem(Birthday birthday, boolean empty) {
        super.updateItem(birthday, empty);

        if (empty || birthday == null) {
            setText(null);
            setGraphic(null);
        } else {
            setFont(font);
            setText(birthday.getListText());
            setContextMenu(generateContextMenu(birthday));
        }
    }


    private ContextMenu generateContextMenu(final Birthday birthday) {
        final ContextMenu contextMenu = new ContextMenu();

        if (isValid(birthday.getMail())) {
            final MenuItem mail = new MenuItem(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.mail"));
            mail.setOnAction((ActionEvent event) -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(birthday.getMail()), null);
                final URI mailURI = URI.create(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "strings.format.mail", birthday.getMail(true)));
                try {
                    Desktop.getDesktop().mail(mailURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            contextMenu.getItems().add(mail);
        }

        if (isValid(birthday.getPhone())) {
            final MenuItem phone = new MenuItem(PropertiesUtils.getInstance().getProperty(PropertiesUtils.PropertyType.MESSAGE, "gui.list.phone"));
            phone.setOnAction((ActionEvent event) -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(birthday.getPhone()), null);
                JOptionPane.showMessageDialog(null, birthday.getPhone(true));
            });
            contextMenu.getItems().add(phone);
        }

        return contextMenu;
    }

    private boolean isValid(final String value) {
        return value != null && !value.trim().isEmpty();
    }
}
