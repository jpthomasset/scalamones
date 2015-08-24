package com.frenchcoder.scalamones.ui

import akka.actor._
import com.frenchcoder.scalamones.elastic.Stat.ClusterHealth
import com.frenchcoder.scalamones.service.KpiProvider.{KpiNotify, KpiMonitor}
import com.frenchcoder.scalamones.service.Manager.{UnMonitor, Monitor}
import com.frenchcoder.scalamones.service.Server
import com.frenchcoder.scalamones.ui

import scalafx.application.Platform
import scalafx.event.Event
import scalafx.scene.chart.{NumberAxis, LineChart}
import scalafx.scene.control.{Label, Tab}
import scalafx.scene.layout.GridPane
import scalafx.scene.shape.Circle
import scalafxml.core.{FXMLLoader, ExplicitDependencies}
import scalafxml.core.macros.sfxml
import scalafx.Includes._

@sfxml
class TabController(private val tab: Tab,
                    private val container:GridPane,
                    implicit val actorSystem: ActorSystem,
                    implicit val manager: ActorRef,
                    implicit val server: Server) {


  tab.text = server.url.toString()

  val n = ClusterWidgetLoader.load("Cluster")
  container.add(n.widgetPane, 0, 0, 1, 1)
  /*loadFxmlWidget("Cluster", "/cluster-widget.fxml", 0, 0, 1, 1)
  loadFxmlWidget("CPU", "/graph-widget.fxml", 1, 0, 1, 1)
  loadFxmlWidget("Memory", "/graph-widget.fxml", 2, 0, 1, 1)
*/

  def onCloseRequest(event: Event): Unit = {
    println("TabController.OnCloseRequest")
  }

  def onClosed(event: Event): Unit = {
    //uiactor ! Stop
  }

  def test(event: Event): Unit = {
    println("Widget CLOSE !! ")
  }

}
