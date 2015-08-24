package com.frenchcoder.scalamones.ui

import akka.actor.{ActorRef, ActorSystem}
import com.frenchcoder.scalamones.service.Server

import scalafx.scene.chart.LineChart
import scalafxml.core.macros.sfxml

@sfxml
class GraphWidget(private val graph:LineChart[Number, Number],
                  private val actorSystem: ActorSystem,
                  private val manager: ActorRef,
                  private val server: Server) extends WidgetContent{

  def close(): Unit = {

  }
}
