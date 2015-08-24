package com.frenchcoder.scalamones.ui

import akka.actor.{ActorRef, ActorSystem}
import com.frenchcoder.scalamones.service.Server

trait WidgetContent {
  case class Stop()

  def close() : Unit

  val widgetPane: WidgetPane
}

trait WidgetLoader {
  def load(title: String)(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server) : WidgetContent
}