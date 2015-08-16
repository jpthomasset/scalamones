package com.frenchcoder.scalamones


import akka.actor.{ActorSystem}
import akka.event.Logging
import com.frenchcoder.scalamones.service.Manager
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{ExplicitDependencies, FXMLView}
import scalafx.Includes._


/**
 *
 */
object Main extends JFXApp {


  implicit val system = ActorSystem("scalamones")

  import system.dispatcher
  val log = Logging(system, getClass)
  val manager = system.actorOf(Manager.props)

  def dependencies = new ExplicitDependencies(Map("actorSystem" -> system, "manager" -> manager))

  // Show Main window
  stage = new JFXApp.PrimaryStage() {
    title = "ScalaMonEs - Elastic Monitoring"
    scene = new Scene(
      FXMLView(getClass.getResource("/main.fxml"), dependencies)
    )
    onCloseRequest = handle { system.shutdown() }

  }

}