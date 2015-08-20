package com.frenchcoder.scalamones.ui

import javafx.beans.property.ObjectPropertyBase
import javafx.event.{EventHandler, Event, EventType}

class EventHandlerProperty(val producer: AnyRef, val event: EventType[Event], val eventName: String, val setEventHandler: (EventType[Event], EventHandler[Event]) => Unit) extends ObjectPropertyBase[EventHandler[Event]] {
  protected override def invalidated = setEventHandler (event, get )
  def getBean = producer
  def getName = eventName
}