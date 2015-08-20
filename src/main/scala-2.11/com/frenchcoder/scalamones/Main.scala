package com.frenchcoder.scalamones


import akka.actor.{ActorSystem}
import akka.event.Logging
import com.frenchcoder.scalamones.service.Manager
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{NoDependencyResolver, ExplicitDependencies, FXMLView}
import scalafx.Includes._


/**
 *
 */
object Main extends JFXApp {


  // Show Main window
  stage = new JFXApp.PrimaryStage() {
    title = "ScalaMonEs - Elastic Monitoring"
    scene = new Scene(
      FXMLView(getClass.getResource("/main.fxml"), NoDependencyResolver)
    )
    onCloseRequest = handle { Manager.system.shutdown() }

  }

}