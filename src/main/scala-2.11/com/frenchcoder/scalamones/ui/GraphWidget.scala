package com.frenchcoder.scalamones.ui

import akka.actor.{ActorRef, ActorSystem}
import com.frenchcoder.scalamones.service.Server
import com.frenchcoder.scalamones.ui.ClusterWidgetLoader._

import scalafx.event.Event
import scalafx.scene.chart.LineChart
import scalafxml.core.{FXMLLoader, ExplicitDependencies}
import scalafxml.core.macros.sfxml

object GraphWidgetLoader extends WidgetLoader {
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent  = loadFxml("/graph-widget.fxml")
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
