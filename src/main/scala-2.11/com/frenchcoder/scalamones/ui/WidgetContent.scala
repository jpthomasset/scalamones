package com.frenchcoder.scalamones.ui

import akka.actor.{ActorRef, ActorSystem}
import com.frenchcoder.scalamones.service.Server
import com.frenchcoder.scalamones.ui.ClusterWidgetLoader._

import scalafxml.core.{FXMLLoader, ExplicitDependencies}

trait WidgetContent {
  case class Stop()

  def close() : Unit

  val widgetPane: WidgetPane
}

trait WidgetLoader {
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server) : WidgetContent

  protected def loadFxml(title: String, fxmlPath: String, deps: Map[String, Any] = Map.empty)(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent = {

    val widget:WidgetPane = new WidgetPane()
    widget.setTitle(title)

    def dependencies = new ExplicitDependencies(Map(
      "actorSystem" -> actorSystem,
      "manager" -> manager,
      "server" -> server,
      "widgetPane" -> widget) ++ deps)

    val loader = new FXMLLoader(getClass.getResource(fxmlPath), dependencies)
    val widgetContent: javafx.scene.Node = loader.load()

    widget.getContent().add(widgetContent)

    loader.getController()
  }
}