package com.frenchcoder.scalamones.ui

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import com.frenchcoder.scalamones.elastic.ClusterStat.ClusterStat
import com.frenchcoder.scalamones.elastic.Stat.ClusterHealth
import com.frenchcoder.scalamones.service.KpiProvider.KpiNotify
import com.frenchcoder.scalamones.service.Manager.{Monitor, UnMonitor}
import com.frenchcoder.scalamones.service.Server
import com.frenchcoder.scalamones.ui.ClusterWidgetLoader._

import scalafx.application.Platform
import scalafx.event.Event
import scalafx.scene.chart.XYChart.{Data, Series}
import scalafx.scene.chart.{XYChart, LineChart}
import scalafxml.core.{FXMLLoader, ExplicitDependencies}
import scalafxml.core.macros.sfxml

object CpuWidgetLoader extends WidgetLoader {
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent  = loadFxml("CPU", "/graph-widget.fxml", Map("extractor" -> ((e:ClusterStat) => e.nodes.process.cpu.percent)))
}

object RamWidgetLoader extends WidgetLoader {
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent  = loadFxml("Memory", "/graph-widget.fxml", Map("extractor" -> ((e:ClusterStat) => e.nodes.jvm.mem.heap_used_in_bytes)))
}

@sfxml
class GraphWidget(private val graph:LineChart[Number, Number],
                  private val extractor: (ClusterStat) => Number,
                  private val actorSystem: ActorSystem,
                  private val manager: ActorRef,
                  private val server: Server,
                  val widgetPane: WidgetPane) extends WidgetContent{


  val uiactor = actorSystem.actorOf(Props(new GraphWidgetActor))
  val series = new Series[Number, Number]()
  graph.getData().addAll(series)

  def close(): Unit = {
    uiactor ! Stop
  }

  val dateFormat = new SimpleDateFormat("HH:mm:ss");

  class GraphWidgetActor extends Actor {

    manager ! Monitor[ClusterStat](server.id)

    def receive = {
      case Stop =>
        manager ! UnMonitor[ClusterStat](server.id)
        context.stop(self)
      case KpiNotify(stat: ClusterStat) => Platform.runLater {
        println("CPU : " + stat.nodes.process.cpu.percent)
        addData(stat.timestamp, extractor(stat))

      }
    }
  }

  def addData(timestamp:Long, value:Number): Unit = {
    series.getData().add(Data[Number, Number](timestamp, value))
    if(series.getData().size() > 10) series.getData().remove(0)

    val mints = series.getData().get(0).getXValue.longValue
    val axis = graph.getXAxis.asInstanceOf[TimeAxis]


    axis.setLowerBound(mints)
    if((timestamp - mints) < 10*5000) {
      axis.setUpperBound(mints + 10*5000)
    } else {
      axis.setUpperBound(timestamp)
    }

    println(s"Force Bound ${axis.getTickMarkLabel(axis.getLowerBound().toLong)} -> ${axis.getUpperBound().toLong}}")
  }


}
