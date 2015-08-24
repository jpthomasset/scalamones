package com.frenchcoder.scalamones.ui

import akka.actor.{ActorRef, ActorSystem}
import com.frenchcoder.scalamones.service.Server

import scalafx.event.Event
import scalafx.scene.chart.LineChart
import scalafxml.core.{FXMLLoader, ExplicitDependencies}
import scalafxml.core.macros.sfxml

object GraphWidgetLoader extends WidgetLoader {
  def load(title: String)(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent = {

    val widget:WidgetPane = new WidgetPane()
    widget.setTitle(title)

    def dependencies = new ExplicitDependencies(Map(
      "actorSystem" -> actorSystem,
      "manager" -> manager,
      "server" -> server,
      "widgetPane" -> widget))

    val loader = new FXMLLoader(getClass.getResource("/graph-widget.fxml"), dependencies)
    val widgetContent: javafx.scene.Node = loader.load()

    widget.getContent().add(widgetContent)

    loader.getController()
  }
}

@sfxml
class GraphWidget(private val graph:LineChart[Number, Number],
                  private val actorSystem: ActorSystem,
                  private val manager: ActorRef,
                  private val server: Server,
                  val widgetPane: WidgetPane) extends WidgetContent{

  def close(): Unit = {

  }
}
