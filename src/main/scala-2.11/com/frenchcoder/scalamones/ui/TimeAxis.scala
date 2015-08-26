package com.frenchcoder.scalamones.ui

import java.text.SimpleDateFormat
import java.util.Date
import javafx.scene.chart
import javafx.scene.chart.{NumberAxis, ValueAxis}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 *
 */
class TimeAxis extends ValueAxis[java.lang.Long] {
  override def calculateMinorTickMarks(): java.util.List[java.lang.Long] = List.empty[java.lang.Long]

 val n = new NumberAxis
  override def getTickMarkLabel(t: java.lang.Long): String = {
    println(s"Formating ${t}")
    (new SimpleDateFormat("hh:mm:ss")).format(new Date(t))
  }

  override def getRange = {
    println("Get Range")
    (lowerBoundProperty().get(), upperBoundProperty().get())
  }

  override def setRange(o: scala.Any, b: Boolean): Unit = {
    if(o != null) {
      val t = o.asInstanceOf[(Double, Double)]
      println(s"setRange ${t._1} -> ${t._2}")
      lowerBoundProperty().set(t._1)
      upperBoundProperty().set(t._2)
    }
  }

  override def calculateTickValues(v: Double, o: scala.Any): java.util.List[java.lang.Long] = {
    if(o != null) {
      val t = o.asInstanceOf[(Double, Double)]
      println(s"calculateTickValues ${t._1} -> ${t._2}")
      val s = (t._2 - t._1) / 10
      (for(n <- t._1 until(t._2, s))
        yield n.toLong.asInstanceOf[java.lang.Long]
      ).asJava
    } else {
      List.empty[java.lang.Long]
    }
  }
}
