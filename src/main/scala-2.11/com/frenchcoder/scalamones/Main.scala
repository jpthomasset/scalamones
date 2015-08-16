package com.frenchcoder.scalamones

import com.frenchcoder.scalamones.service.Manager.{Monitor, AddServer}
import com.frenchcoder.scalamones.service.{Manager, KpiProvider}
import spray.httpx.unmarshalling._

import scala.util.{Success, Failure}
import scala.concurrent.duration._
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.event.Logging
import akka.io.IO
import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.can.Http
import spray.client.pipelining._
import spray.util._
import elastic.Stat._
import elastic.ElasticJsonProtocol
import spray.httpx.SprayJsonSupport

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.macros.sfxml
import scalafxml.core.{ExplicitDependencies, FXMLLoader, DependenciesByType, FXMLView}
import scalafx.Includes._
import scalafx.event.ActionEvent
import javafx.{scene => jfxs}

/**
 *
 */
object Main extends JFXApp {


  implicit val system = ActorSystem("simple-spray-client")

  import system.dispatcher
  val log = Logging(system, getClass)


  val manager = system.actorOf(Manager.props)
/*
  manager ! AddServer("127.0.0.1", 9200)
  manager ! Monitor[NodesStat](1)

  */



  def dependencies = new ExplicitDependencies(Map("actorSystem" -> system, "manager" -> manager))

  // Show Main window
  stage = new JFXApp.PrimaryStage() {
    title = "ScalaMonEs - Elastic Monitoring"
    scene = new Scene(
      FXMLView(getClass.getResource("/main.fxml"), dependencies)
    )
    onCloseRequest = handle { println("Closing") }

  }

}