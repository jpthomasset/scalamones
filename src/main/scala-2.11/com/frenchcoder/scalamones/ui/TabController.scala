package com.frenchcoder.scalamones.ui

import akka.actor._
import com.frenchcoder.scalamones.elastic.Stat.ClusterHealth
import com.frenchcoder.scalamones.service.KpiProvider.{KpiNotify, KpiMonitor}
import com.frenchcoder.scalamones.service.Manager.{UnMonitor, Monitor}
import com.frenchcoder.scalamones.service.Server

import scalafx.application.Platform
import scalafx.event.Event
import scalafx.scene.control.{Label, Tab}
import scalafx.scene.shape.Circle
import scalafxml.core.macros.sfxml

@sfxml
class TabController(private val tab: Tab,
                    private val clusterNameLabel:Label,
                    private val clusterStatusShape: Circle,
                    private val clusterStatusLabel: Label,
                    private val clusterUptimeLabel:Label,
                    private val clusterShardsLabel:Label,
                    private implicit val actorSystem: ActorSystem,
                    private val manager: ActorRef,
                    private val server: Server) {


  tab.text = server.host
  val uiactor = actorSystem.actorOf(Props(new TabControllerActor()))

  case class Stop()

  class TabControllerActor extends Actor {

    manager ! Monitor[ClusterHealth](server.id)
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

  def onCloseRequest(event: Event): Unit = {
    println("TabController.OnCloseRequest")
  }

  def onClosed(event: Event): Unit = {
    uiactor ! Stop
  }

}
