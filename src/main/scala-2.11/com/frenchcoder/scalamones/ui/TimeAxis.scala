package com.frenchcoder.scalamones.ui

import java.text.SimpleDateFormat
import java.util.Date
import javafx.scene.chart.ValueAxis
import scala.collection.JavaConverters._

import com.frenchcoder.scalamones.utils.Conversion._
/**
 *
 */
class TimeAxis extends ValueAxis[java.lang.Long] {
  case class Range(scale:Double, min: Double, max: Double, majorTickStep: Double, minorTickStep: Double)

  var currentRange:Range = null

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
      tickValues(r.min, r.max, r.majorTickStep)
    } else {
      val r = autoRange(getLowerBound,getUpperBound, getWidth, 20).asInstanceOf[Range]
      tickValues(r.min, r.max, r.majorTickStep)
    }
  }

  def tickValues(min:Double, max:Double, step:Double): java.util.List[java.lang.Long] = {

    val seq = if(min == max) Seq(min) else min to(max, step)

    (for(n <- seq) yield n.toLong.asInstanceOf[java.lang.Long]).asJava
  }

  override def calculateMinorTickMarks(): java.util.List[java.lang.Long] = {
    if(currentRange != null) {
      (for(n <- currentRange.min to(currentRange.max, currentRange.minorTickStep))
        yield n.toLong.asInstanceOf[java.lang.Long]).asJava
    } else {
      List.empty[java.lang.Long].asJava
    }
  }

  override def autoRange(minValue: Double, maxValue: Double, length: Double, labelSize: Double) : AnyRef = {

    val l = if(length > 0) length else 200
    val range = maxValue - minValue

    val majorStep = (range) / (l / (4*labelSize)).toLong
    Range(calculateNewScale(length, minValue, maxValue), minValue, maxValue, majorStep, majorStep / 2)

  }
}
