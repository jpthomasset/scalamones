package com.frenchcoder.scalamones.ui

import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import com.frenchcoder.scalamones.service.Manager.{AddServer, ServerList, MonitorServerListChange}

import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control.Label
import scalafxml.core.macros.sfxml

@sfxml
class MainController( private val statusLabel : Label,
                      private implicit val actorSystem: ActorSystem,
                      private val manager: ActorRef) {

  val uiactor = actorSystem.actorOf(Props(new MainControllerActor()))

  // embedded actor
  class MainControllerActor extends Actor {
    manager ! MonitorServerListChange
    def receive = {
      case ServerList(servers) => Platform.runLater {
        statusLabel.text = "Received new server list : " + servers.size
      }
    }
  }

  def onAddServer(event: ActionEvent) {
    statusLabel.text = "onAddServer"
    manager ! AddServer("http://127.0.0.1", 9200)
  }
}
