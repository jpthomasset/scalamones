package com.frenchcoder.scalamones.ui

import javafx.beans.property.{ObjectPropertyBase, StringProperty, ObjectProperty}
import javafx.collections.ObservableList
import javafx.event.{ActionEvent, EventHandler, Event, EventType}
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.{StackPane, VBox}

import scalafxml.core.{NoDependencyResolver, FXMLLoader}

object WidgetPane {
  /* Event Handling */
  val CLOSE_REQUEST_EVENT    = new EventType(Event.ANY, "WP_CLOSE_REQUEST")
  val MINIMIZE_REQUEST_EVENT = new EventType(Event.ANY, "WP_MINIMIZE_REQUEST")
  val MAXIMIZE_REQUEST_EVENT = new EventType(Event.ANY, "WP_MAXIMIZE_REQUEST")
}

/**
 *
 */
class WidgetPane extends VBox {
  import WidgetPane._
  @FXML private var contentPane: StackPane = null;
  @FXML private var titleLabel: Label = null

  val loader = new FXMLLoader(getClass.getResource("/widgetpane.fxml"), NoDependencyResolver)
  loader.setRoot(this);
  loader.setController(this);
  loader.load();

  private val onCloseRequest    = new EventHandlerProperty(this, CLOSE_REQUEST_EVENT,   "onCloseRequest",     setBasicEventHandler)
  private val onMinimizeRequest = new EventHandlerProperty(this, MINIMIZE_REQUEST_EVENT, "onMinimizeRequest", setBasicEventHandler)
  private val onMaximizeRequest = new EventHandlerProperty(this, MAXIMIZE_REQUEST_EVENT, "onMaximizeRequest", setBasicEventHandler)

  def getTitle: String = titleProperty.get
  def setTitle(value: String) = titleProperty.set(value)
  def titleProperty: StringProperty = titleLabel.textProperty

  def getContent() = contentPane.getChildren

  def onCloseRequestProperty = onCloseRequest
  def getOnCloseRequest = onCloseRequestProperty.get()
  def setOnCloseRequest(x:EventHandler[Event]) = onCloseRequestProperty.set(x)

  def onMinimizeRequestProperty = onMinimizeRequest
  def getOnMinimizeRequest = onMinimizeRequestProperty.get()
  def setOnMinimizeRequest(x:EventHandler[Event]) = onMinimizeRequestProperty.set(x)

  def onMaximizeRequestProperty = onMaximizeRequest
  def getOnMaximizeRequest = onMaximizeRequestProperty.get()
  def setOnMaximizeRequest(x:EventHandler[Event]) = onMaximizeRequestProperty.set(x)

  @FXML def close(event: ActionEvent) = consumeAndFire(event, CLOSE_REQUEST_EVENT)
  @FXML def minimize(event: ActionEvent) = consumeAndFire(event, MINIMIZE_REQUEST_EVENT)
  @FXML def maximize(event: ActionEvent) = consumeAndFire(event, MAXIMIZE_REQUEST_EVENT)

  private def consumeAndFire(event: ActionEvent, eventType: EventType[Event]) {
    event.consume()
    Event.fireEvent(this, new Event(eventType))
  }

  def setBasicEventHandler(et: EventType[Event], eh: EventHandler[Event]) = setEventHandler(et, eh)

}

