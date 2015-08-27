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
class TimeAxis extends ValueAxis[java.lang.Long](1440655187000L, 1440655387000L) {
  override def calculateMinorTickMarks(): java.util.List[java.lang.Long] = {
    if(currentRange != null) {
      val ticks = (for(n <- currentRange.min until(currentRange.max, currentRange.minorTickStep))
        yield n.toLong.asInstanceOf[java.lang.Long]
        )

      println("MinorTicks " +  ticks.size)
      ticks.asJava
    } else {
      List.empty[java.lang.Long]
    }
  }

  case class Range(scale:Double, min: Double, max: Double, majorTickStep: Double, minorTickStep: Double)

  var currentRange:Range = Range(1, 1, 100, 20, 10)
  setLowerBound(new Date().getTime)
  setUpperBound(new Date().getTime)

  override def getTickMarkLabel(t: java.lang.Long): String = {
    //println(s"Formating ${t}")
    (new SimpleDateFormat("hh:mm:ss")).format(new Date(t))
  }

  override def getRange = {
    //println("Get Range")
    currentRange
  }

  override def setRange(o: scala.Any, b: Boolean): Unit = {
    if(o != null) {
      currentRange = o.asInstanceOf[Range]
      println("setRange with value " + currentRange)
      setLowerBound(currentRange.min)
      setUpperBound(currentRange.max)
      setScale(currentRange.scale)
    }
  }

  override def calculateTickValues(v: Double, o: scala.Any): java.util.List[java.lang.Long] = {
    println("calculateTickValues")
    if(o != null) {
      val r = o.asInstanceOf[Range]
      println(s"calculateTickValues ${r.min} -> ${r.max}")

      val seq = if(r.min == r.max) Seq(r.min) else r.min until(r.max, r.majorTickStep)

      val ticks = (for(n <- seq)
        yield n.toLong.asInstanceOf[java.lang.Long]
      )

      println("Ticks " +  ticks.size)
      ticks.asJava
    } else {
      List.empty[java.lang.Long]
    }
  }

  override def autoRange(minValue: Double, maxValue: Double, length: Double, labelSize: Double) : AnyRef = {
    println(s"autorange(${minValue}, ${maxValue}, ${length}, ${labelSize})")

    val range = maxValue - minValue
    val paddedRange = if(range == 0) 2 else range*1.02
    val padding = (paddedRange - range) / 2
    val paddedMin = minValue - padding
    val paddedMax = maxValue + padding

    val majorStep = (paddedMax - paddedMin) / (length / labelSize)

    Range(calculateNewScale(length, paddedMin, paddedMax), paddedMin, paddedMax, majorStep, majorStep/2)

  }
}
