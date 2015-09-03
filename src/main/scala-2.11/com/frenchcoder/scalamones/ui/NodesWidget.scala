package com.frenchcoder.scalamones.ui

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.frenchcoder.scalamones.elastic.Stat.{ClusterHealth, NodesStat}
import com.frenchcoder.scalamones.service.KpiProvider.KpiNotify
import com.frenchcoder.scalamones.service.Manager.{UnMonitor, Monitor}
import com.frenchcoder.scalamones.service.Server

import scalafx.application.Platform
import scalafxml.core.macros.sfxml

object NodesWidgetLoader extends WidgetLoader {
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent = loadFxml("Nodes", "/nodes-widget.fxml")
}

@sfxml
class NodesWidget(private val actorSystem: ActorSystem,
                  private val manager: ActorRef,
                  private val server: Server,
                  val widgetPane: WidgetPane) extends WidgetContent {


  val uiactor = actorSystem.actorOf(Props(new NodesWidgetActor()))

  def close: Unit = {
    uiactor ! Stop
  }

  class NodesWidgetActor extends Actor {

    manager ! Monitor[NodesStat](server.id)

    def receive = {
      case Stop =>
        manager ! UnMonitor[NodesStat](server.id)
        context.stop(self)

      case KpiNotify(health:ClusterHealth) => Platform.runLater {

      }
    }
  }


}
