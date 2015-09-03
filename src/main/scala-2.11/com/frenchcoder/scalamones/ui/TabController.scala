package com.frenchcoder.scalamones.ui

import akka.actor._
import com.frenchcoder.scalamones.service.Server
import scalafx.event.Event
import scalafx.scene.control.Tab
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml
import scalafx.Includes._

@sfxml
class TabController(private val tab: Tab,
                    private val container:GridPane,
                    implicit val actorSystem: ActorSystem,
                    implicit val manager: ActorRef,
                    implicit val server: Server) {


  tab.text = server.url.toString()

  addWidget(ClusterWidgetLoader.load, 0, 0, 1, 1)
  addWidget(CpuWidgetLoader.load, 1, 0, 1, 1)
  addWidget(RamWidgetLoader.load, 2, 0, 1, 1)

  addWidget(NodesWidgetLoader.load, 0, 1, 3, 1)

  /*loadFxmlWidget("Cluster", "/cluster-widget.fxml", 0, 0, 1, 1)
  loadFxmlWidget("CPU", "/graph-widget.fxml", 1, 0, 1, 1)
  loadFxmlWidget("Memory", "/graph-widget.fxml", 2, 0, 1, 1)
*/

  def addWidget(w: WidgetContent, col:Int, row:Int, colspan:Int, rowspan:Int): Unit = {
    w.widgetPane.onCloseRequestProperty() = (e:Event) => {container.children.remove(w.widgetPane)}
    container.add(w.widgetPane, col, row, colspan, rowspan)
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
