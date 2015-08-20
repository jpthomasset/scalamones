package com.frenchcoder.scalamones.java.ui;

import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 */
public class EventHandlerProperty extends ObjectPropertyBase<EventHandler<Event>> {

    private final EventProducerInterface eventProducer;
    private final String eventName;

    public EventHandlerProperty(EventProducerInterface eventProducer, String eventName) {
        this.eventProducer = eventProducer;
        this.eventName = eventName;
    }

    protected void invalidated() {
        eventProducer.setBasicEventHandler(WidgetPane.CLOSE_REQUEST_EVENT, (EventHandler) get());
    }

    public Object getBean() {
        return eventProducer;
    }

    public String getName() {
        return eventName;
    }
}
