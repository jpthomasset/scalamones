package com.frenchcoder.scalamones.ui

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.frenchcoder.scalamones.service.Server

import scalafx.event.ActionEvent
import scalafx.scene.control.Tab
import scalafxml.core.macros.sfxml

@sfxml
class TabController(private val tab: Tab,
                    private implicit val actorSystem: ActorSystem,
                    private val manager: ActorRef,
                     private val server: Server) {


  tab.text = server.host
  //val uiactor = actorSystem.actorOf(Props(new TabControllerActor()))

  class TabControllerActor extends Actor {
    def receive = {
      case "" =>
    }
  }

  def onCloseRequest(event: ActionEvent): Unit = {
    println("TabController.OnCloseRequest")
  }

  def onClosed(event: ActionEvent): Unit = {
    println("TabController.OnClosed")
  }

}
