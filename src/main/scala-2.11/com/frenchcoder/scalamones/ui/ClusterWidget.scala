package com.frenchcoder.scalamones.ui

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.frenchcoder.scalamones.elastic.Stat.ClusterHealth
import com.frenchcoder.scalamones.service.KpiProvider.KpiNotify
import com.frenchcoder.scalamones.service.Manager.UnMonitor
import com.frenchcoder.scalamones.service.Server

import scalafx.application.Platform
import scalafx.scene.chart.{NumberAxis, LineChart}
import scalafx.scene.control.{Label, Tab}
import scalafx.scene.shape.Circle
import scalafxml.core.{ExplicitDependencies, FXMLLoader}
import scalafxml.core.macros.sfxml

object ClusterWidgetLoader extends WidgetLoader {
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent = loadFxml("/cluster-widget.fxml")

}

@sfxml
class ClusterWidget(private val clusterNameLabel:Label,
                    private val clusterStatusShape: Circle,
                    private val clusterStatusLabel: Label,
                    private val clusterUptimeLabel:Label,
                    private val clusterShardsLabel:Label,
                    private val actorSystem: ActorSystem,
                    private val manager: ActorRef,
                    private val server: Server,
                    val widgetPane: WidgetPane) extends WidgetContent {

  val uiactor = actorSystem.actorOf(Props(new ClusterWidgetActor()))

  def close: Unit = {
    uiactor ! Stop
  }


  class ClusterWidgetActor extends Actor {

    // manager ! Monitor[ClusterHealth](server.id)

    def receive = {
      case Stop =>
        manager ! UnMonitor[ClusterHealth](server.id)
        context.stop(self)

      case KpiNotify(health:ClusterHealth) => Platform.runLater {
        clusterNameLabel.text = health.cluster_name
        clusterShardsLabel.text = s"${health.active_shards} / ${health.active_shards + health.unassigned_shards}"
        clusterStatusShape.styleClass.clear
        clusterStatusShape.styleClass += "status-" + health.status
        clusterStatusLabel.text = health.status
      }
    }
  }
}
