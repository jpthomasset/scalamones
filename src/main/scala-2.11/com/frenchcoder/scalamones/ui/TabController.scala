package com.frenchcoder.scalamones.ui

import javafx.fxml.{FXMLLoader, FXML}

import akka.actor._
import com.frenchcoder.scalamones.elastic.Stat.ClusterHealth
import com.frenchcoder.scalamones.service.KpiProvider.{KpiNotify, KpiMonitor}
import com.frenchcoder.scalamones.service.Manager.{UnMonitor, Monitor}
import com.frenchcoder.scalamones.service.{Manager, Server}

import scalafx.application.Platform
import scalafx.event.Event
import scalafx.scene.chart.{NumberAxis, LineChart}
import scalafx.scene.control.{Label, Tab}
import scalafx.scene.shape.Circle

class TabController extends Tab {
  @FXML private val clusterNameLabel:Label = null
  @FXML private val clusterStatusShape: Circle = null
  @FXML private val clusterStatusLabel: Label = null
  @FXML private val clusterUptimeLabel:Label = null
  @FXML private val clusterShardsLabel:Label = null
  @FXML private val cpuGraph: LineChart[NumberAxis, NumberAxis] = null
  @FXML private val memGraph: LineChart[NumberAxis, NumberAxis] = null

  private var server: Server = null

  def this(s: Server) = {
    this()
    server = s
    text = server.url.toString()

    val loader = new FXMLLoader(getClass.getResource("/tab.fxml"))
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }


  val uiActor = Manager.system.actorOf(Props(new TabControllerActor()))

  case class Stop()

  class TabControllerActor extends Actor {

    // Start monitoring server
    // manager ! Monitor[ClusterHealth](server.id)
    def receive = {
      case Stop =>
        Manager.actor ! UnMonitor[ClusterHealth](server.id)
        context.stop(self)

      case KpiNotify(health:ClusterHealth) => Platform.runLater {
        text = s"${health.cluster_name} [${server.url}]"
        clusterNameLabel.text = health.cluster_name
        clusterShardsLabel.text = s"${health.active_shards} / ${health.active_shards + health.unassigned_shards}"
        clusterStatusShape.styleClass.clear
        clusterStatusShape.styleClass += "status-" + health.status
        clusterStatusLabel.text = health.status
      }

    }
  }

  @FXML
  def onCloseRequest(event: Event): Unit = {
    println("TabController.OnCloseRequest")
  }

  @FXML
  def onClosed(event: Event): Unit = {
    uiActor ! Stop
  }

  @FXML
  def test(): Unit = {
    println("Widget CLOSE !! ")
  }

}
