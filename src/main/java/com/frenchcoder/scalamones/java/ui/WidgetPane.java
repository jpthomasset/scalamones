package com.frenchcoder.scalamones.java.ui;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 *
 */
public class WidgetPane extends VBox {
    @FXML private StackPane contentPane;
    @FXML private Label titleLabel;

    public WidgetPane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/widgetpane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public void setTitle(String value) {
        titleProperty().set(value);
    }

    public StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    public ObservableList<Node> getContent() {
        return contentPane.getChildren();
    }
}
