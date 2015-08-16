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
import scalafxml.core.{FXMLLoader, DependenciesByType, FXMLView}
import scalafx.Includes._
import scalafx.event.ActionEvent
import javafx.{scene => jfxs}
/**
 *
 */
object Main extends JFXApp {

  /* WORKING
  implicit val system = ActorSystem("simple-spray-client")
  import system.dispatcher
  val log = Logging(system, getClass)


  val manager = system.actorOf(Manager.props)
  manager ! AddServer("127.0.0.1", 9200)
  manager ! Monitor[NodesStat](1)

  */

  class TestController(val test:String) {

    def onAddServer(event: ActionEvent) {
      println("Custom controller " + test)
    }
  }

  def dependencies = new DependenciesByType(Map.empty)

  val loader = new FXMLLoader(getClass.getResource("/main.fxml"), dependencies)
  loader.setController(new TestController("test"))
  loader.load()

  stage = new JFXApp.PrimaryStage() {
    title = "Test window"
    scene = new Scene(
      loader.getRoot[jfxs.Parent]()

    )
    FXMLView(getClass.getResource("/main.fxml"), dependencies)
  }

}