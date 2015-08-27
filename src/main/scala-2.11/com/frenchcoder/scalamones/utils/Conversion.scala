package com.frenchcoder.scalamones.utils

object Conversion {
  implicit def doubleToJavaLong(d:Double) : java.lang.Long = d.toLong.asInstanceOf[java.lang.Long]
}
