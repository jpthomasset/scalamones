package com.frenchcoder.scalamones.ui

import java.text.SimpleDateFormat
import java.util.Date
import javafx.scene.chart.ValueAxis
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 *
 */
class TimeAxis extends ValueAxis[java.lang.Long] {
  case class Range(scale:Double, min: Double, max: Double, majorTickStep: Double, minorTickStep: Double)

  var currentRange:Range = null
  setRange(autoRange(1, 100, 200, 20).asInstanceOf[Range], false)

  override def getTickMarkLabel(t: java.lang.Long): String = new SimpleDateFormat("kk:mm:ss").format(new Date(t))

  override def getRange = currentRange

  override def setRange(o: scala.Any, b: Boolean): Unit = {
    if(o != null) {
      currentRange = o.asInstanceOf[Range]
      setLowerBound(currentRange.min)
      currentLowerBound.set(currentRange.min)
      setUpperBound(currentRange.max)
      setScale(currentRange.scale)
    }
  }

  override def calculateTickValues(v: Double, o: scala.Any): java.util.List[java.lang.Long] = {
    if(o != null) {
      val r = o.asInstanceOf[Range]
      val seq = if(r.min == r.max) Seq(r.min) else r.min until(r.max, r.majorTickStep)

      (for(n <- seq) yield n.toLong.asInstanceOf[java.lang.Long]).asJava
    } else {
      List.empty[java.lang.Long]
    }
  }

  override def calculateMinorTickMarks(): java.util.List[java.lang.Long] = {
    if(currentRange != null) {
      (for(n <- currentRange.min until(currentRange.max, currentRange.minorTickStep))
        yield n.toLong.asInstanceOf[java.lang.Long]).asJava
    } else {
      List.empty[java.lang.Long]
    }
  }

  override def autoRange(minValue: Double, maxValue: Double, length: Double, labelSize: Double) : AnyRef = {

    val range = maxValue - minValue
    val paddedRange = if(range == 0) 2 else range*1.02
    val padding = (paddedRange - range) / 2
    val paddedMin = minValue - padding
    val paddedMax = maxValue + padding

    val majorStep = (paddedMax - paddedMin) / (length / (4*labelSize))
    println(s"length ${length} - ${labelSize}")
    println(s"Step ${(paddedMax - paddedMin)} / ${(length / (4*labelSize))} => ${majorStep}")
    Range(calculateNewScale(length, paddedMin, paddedMax), paddedMin, paddedMax, majorStep, majorStep / 2)

  }
}
