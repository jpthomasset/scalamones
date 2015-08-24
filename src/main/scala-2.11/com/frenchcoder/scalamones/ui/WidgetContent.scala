package com.frenchcoder.scalamones.ui

trait WidgetContent {
  case class Stop()

  def close() : Unit
}
