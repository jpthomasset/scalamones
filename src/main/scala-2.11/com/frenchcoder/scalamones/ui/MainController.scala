package com.frenchcoder.scalamones.ui

import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import com.frenchcoder.scalamones.service.Manager.{ServerAdded, AddServer, ServerList, MonitorServerListChange}
import com.frenchcoder.scalamones.service.Server
import spray.http.Uri

import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control._
import scalafxml.core.{FXMLLoader, ExplicitDependencies, FXMLView}
import scalafxml.core.macros.sfxml
import scalafx.Includes._

@sfxml
class MainController(private val serversMenu: Menu,
                      private val statusLabel: Label,
                      private val tabPane: TabPane,
                      private implicit val actorSystem: ActorSystem,
                      private val manager: ActorRef) {

  val uiactor = actorSystem.actorOf(Props(new MainControllerActor()))

  // embedded actor
  class MainControllerActor extends Actor {
    manager ! MonitorServerListChange
    def receive = {
      case ServerList(servers) => Platform.runLater { onServerList(servers) }
      case AddServer(url) => manager ! AddServer(url)
      case ServerAdded(server) => Platform.runLater { onServerAdded(server) }
    }
  }

  def onServerList(servers:Set[Server]): Unit = {
    statusLabel.text = "Received new server list : " + servers.size
    if(serversMenu.items.size > 1) {
      serversMenu.items.remove(1, serversMenu.items.size)
    }
    serversMenu.items.add(new SeparatorMenuItem())
    servers map { s => serversMenu.items.add(new MenuItem(s.url.toString())) }

  }

  def onAddServer(event: ActionEvent) = {
    val dialog = new TextInputDialog(defaultValue = "http://127.0.0.1:9200") {
      //initOwner(stage)
      title = "Add a new server"
      headerText = "Enter the url of the server to monitor,\nincluding protocol and port.\n\n(i.e.'http://127.0.0.1:9200)"
      contentText = "Server URL:"
    }

    val result = dialog.showAndWait()

    result match {
      case Some(url) => uiactor ! AddServer(Uri(url))
    }
    //uiactor ! AddServer(Uri("http://127.0.0.1:9200"))
  }

  def onServerAdded(server: Server) = {
    def dependencies = new ExplicitDependencies(Map(
      "actorSystem" -> actorSystem,
      "manager" -> manager,
      "server" -> server))
    val loader = new FXMLLoader(getClass.getResource("/tab.fxml"), dependencies)
    val tab: javafx.scene.control.Tab = loader.load()
    tabPane.tabs.add(tab)
  }
}
