package com.frenchcoder.scalamones.java.ui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 *
 */
public interface EventProducerInterface {
    void setBasicEventHandler(EventType<Event> eventType, EventHandler<Event> eventHandler);
}
