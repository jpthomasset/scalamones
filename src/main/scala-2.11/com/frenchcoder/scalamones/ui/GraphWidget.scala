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
import scalafx.scene.control.Label
import scalafxml.core.{FXMLLoader, ExplicitDependencies}
import scalafxml.core.macros.sfxml

object CpuWidgetLoader extends WidgetLoader {
  val d = Map("extractor" -> ((e:ClusterStat) => e.nodes.process.cpu.percent),
              "formatter" -> ((n:Number) => (n.toString, "%")))
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent  = loadFxml("CPU", "/graph-widget.fxml", d)
}

object RamWidgetLoader extends WidgetLoader {
  val d = Map("extractor" -> ((e:ClusterStat) => e.nodes.jvm.mem.heap_used_in_bytes),
              "formatter" -> {formatter(_)})
  def load(implicit actorSystem: ActorSystem, manager: ActorRef, server: Server): WidgetContent  = loadFxml("Memory", "/graph-widget.fxml", d)

  private def formatter(n:Number) : (String, String) = {
    val d = n.doubleValue()
    if(d < 1000) (n.toString, " B")
    else {
      val exp = math.min(math.floor(math.log(d) / math.log(1000)), 6).toInt
      val unit = Array("kB", "MB", "GB", "TB", "PB", "EB")
      val value = d / math.pow(1000, exp)
      val rounded = if(value > 5) math.round(value) else math.round(value * 10) / 10
      (rounded.toString, unit(exp -1))
    }
  }
}

@sfxml
class GraphWidget(private val graph:LineChart[Number, Number],
                  private val currentLabel: Label,
                  private val currentUnitLabel: Label,
                  private val minLabel: Label,
                  private val maxLabel: Label,
                  private val extractor: (ClusterStat) => Number,
                  private val formatter: (Number) => (String, String),
                  private val actorSystem: ActorSystem,
                  private val manager: ActorRef,
                  private val server: Server,
                  val widgetPane: WidgetPane) extends WidgetContent{


  val uiactor = actorSystem.actorOf(Props(new GraphWidgetActor))
  val series = new Series[Number, Number]()
  var min = Double.MaxValue
  var max = Double.MinValue

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
        addData(stat.timestamp, extractor(stat))

      }
    }
  }

  def addData(timestamp:Long, value:Number): Unit = {
    series.getData().add(Data[Number, Number](timestamp, value))
    if(series.getData().size() > 10) series.getData().remove(0)

    val mints = series.getData().get(0).getXValue.longValue
    val axis = graph.getXAxis.asInstanceOf[TimeAxis]

    if(value.doubleValue() < min) min = value.doubleValue()
    if(value.doubleValue() > max) max = value.doubleValue()

    formatter(value) match { case (a,b) => currentLabel.text = a; currentUnitLabel.text = b}
    formatter(min) match { case (a,b) => minLabel.text = a + " " +b }
    formatter(max) match { case (a,b) => maxLabel.text = a + " " +b }

    axis.setLowerBound(mints)
    if((timestamp - mints) < 10*5000) {
      axis.setUpperBound(mints + 10*5000)
    } else {
      axis.setUpperBound(timestamp)
    }
  }


}
