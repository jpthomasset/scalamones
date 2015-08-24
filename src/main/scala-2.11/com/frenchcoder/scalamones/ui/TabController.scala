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
                    private implicit val actorSystem: ActorSystem,
                    private val manager: ActorRef,
                    private val server: Server) {


  tab.text = server.url.toString()

  loadFxmlWidget("Cluster", "/cluster-widget.fxml", 0, 0, 1, 1)
  loadFxmlWidget("CPU", "/graph-widget.fxml", 1, 0, 1, 1)
  loadFxmlWidget("Memory", "/graph-widget.fxml", 2, 0, 1, 1)


  def loadFxmlWidget(title: String, fxmlPath: String, col: Int, row: Int, colspan:Int, rowspan:Int): Unit = {
    def dependencies = new ExplicitDependencies(Map(
      "actorSystem" -> actorSystem,
      "manager" -> manager,
      "server" -> server))
    val loader = new FXMLLoader(getClass.getResource(fxmlPath), dependencies)
    val widgetContent: javafx.scene.Node = loader.load()
    val controller:WidgetContent = loader.getController()

    addWidget(title, widgetContent, controller, col, row, colspan, rowspan)
  }

  def addWidget(title: String, content: javafx.scene.Node, controller: WidgetContent, col: Int, row: Int, colspan:Int, rowspan:Int): Unit = {
    val widget:WidgetPane = new WidgetPane {
      titleProperty() = title
      getContent().add(content)
      onCloseRequestProperty() = { (event:Event) =>
        controller.close()
        container.getChildren().remove(this)
      }
    }

    container.add(widget, col, row, colspan, rowspan)
  }


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
