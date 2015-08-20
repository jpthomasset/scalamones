package com.frenchcoder.scalamones.java.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
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

    public static final EventType<Event> CLOSE_REQUEST_EVENT = new EventType(Event.ANY, "CLOSE_REQUEST");
    public static final EventType<Event> MINIMIZE_REQUEST_EVENT = new EventType(Event.ANY, "MINIMIZE_REQUEST");
    public static final EventType<Event> MAXIMIZE_REQUEST_EVENT = new EventType(Event.ANY, "MAXIMIZE_REQUEST");

    private ObjectProperty<EventHandler<Event>> onCloseRequest;
    private ObjectProperty<EventHandler<Event>> onMinimizeRequest;
    private ObjectProperty<EventHandler<Event>> onMaximizeRequest;

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

    public ObjectProperty<EventHandler<Event>> onCloseRequestProperty() {
        if(onCloseRequest == null) {
            onCloseRequest = new ObjectPropertyBase<EventHandler<Event>>() {
                protected void invalidated() {
                    WidgetPane.this.setEventHandler(WidgetPane.CLOSE_REQUEST_EVENT, (EventHandler) this.get());
                }

                public Object getBean() {
                    return WidgetPane.this;
                }

                public String getName() {
                    return "onCloseRequest";
                }
            };
        }
        return onCloseRequest;
    }
    public EventHandler<Event> getOnCloseRequest() {
        return onCloseRequest != null ? onCloseRequestProperty().get() : null;
    }
    public void setOnCloseRequest(EventHandler<Event> eventHandler) {
        onCloseRequestProperty().set(eventHandler);
    }

    public ObjectProperty<EventHandler<Event>> onMinimizeRequestProperty() {
        if(onMinimizeRequest == null) {
            onMinimizeRequest = new ObjectPropertyBase<EventHandler<Event>>() {
                protected void invalidated() {
                    WidgetPane.this.setEventHandler(WidgetPane.MINIMIZE_REQUEST_EVENT, (EventHandler) this.get());
                }

                public Object getBean() {
                    return WidgetPane.this;
                }

                public String getName() {
                    return "onMinimizeRequest";
                }
            };
        }
        return onMinimizeRequest;
    }
    public EventHandler<Event> getOnMinimizeRequest() {
        return onMinimizeRequest != null ? onMinimizeRequestProperty().get() : null;
    }
    public void setOnMinimizeRequest(EventHandler<Event> eventHandler) {
        onMinimizeRequestProperty().set(eventHandler);
    }

    public ObjectProperty<EventHandler<Event>> onMaximizeRequestProperty() {
        if(onMaximizeRequest == null) {
            onMaximizeRequest = new ObjectPropertyBase<EventHandler<Event>>() {
                protected void invalidated() {
                    WidgetPane.this.setEventHandler(WidgetPane.MAXIMIZE_REQUEST_EVENT, (EventHandler) this.get());
                }

                public Object getBean() {
                    return WidgetPane.this;
                }

                public String getName() {
                    return "onMaximizeRequest";
                }
            };
        }
        return onMaximizeRequest;
    }
    public EventHandler<Event> getOnMaximizeRequest() {
        return onMaximizeRequest != null ? onMaximizeRequestProperty().get() : null;
    }
    public void setOnMaximizeRequest(EventHandler<Event> eventHandler) {
        onMaximizeRequestProperty().set(eventHandler);
    }

    @FXML
    protected void minimize(ActionEvent event) {
        event.consume();
        Event.fireEvent(this, new Event(MINIMIZE_REQUEST_EVENT));
    }

    @FXML
    protected void maximize(ActionEvent event) {
        event.consume();
        Event.fireEvent(this, new Event(MAXIMIZE_REQUEST_EVENT));
    }

    @FXML
    protected void close(ActionEvent event) {
        event.consume();
        Event.fireEvent(this, new Event(CLOSE_REQUEST_EVENT));
    }
}
