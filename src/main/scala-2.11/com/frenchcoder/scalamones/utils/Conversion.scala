package com.frenchcoder.scalamones.utils

import scala.concurrent.duration._
import scala.language.implicitConversions

object Conversion {
  implicit def doubleToJavaLong(d:Double) : java.lang.Long = d.toLong.asInstanceOf[java.lang.Long]

  def millisToHuman(m: Long): String = {
    def _convert(u: List[(TimeUnit, String)], remaining: FiniteDuration, s:String) : String = {
      if(u.isEmpty) s
      else {
        if(remaining >= FiniteDuration(1, u.head._1)) {
          val v = remaining.toUnit(u.head._1).toInt
          _convert(u.tail, remaining - FiniteDuration(v, u.head._1), s + v.toString + u.head._2 + " ")
        } else _convert(u.tail, remaining, s)

      }
    }
    val durationUnit = (DAYS, "d") :: (HOURS, "h") :: (MINUTES, "m") :: (SECONDS, "s") :: Nil

    if(m<1) "0s"
    else if(m<1000) "<1s"
    else _convert(durationUnit, FiniteDuration(m, MILLISECONDS), "").trim
  }
}
