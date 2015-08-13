package com.frenchcoder.scalamones.service

import akka.actor.{Props, Actor}
import com.frenchcoder.scalamones.elastic.ElasticJsonProtocol
import com.frenchcoder.scalamones.elastic.Stat._
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.httpx.unmarshalling._
import scala.util.{Failure, Success}
import akka.util.Timeout


object KpiProvider {
  def props[T: FromResponseUnmarshaller](url: String) = Props(new KpiProvider[T](url))

  case class KpiReceived[T](kpi: T)
}

class KpiProvider[T: FromResponseUnmarshaller](val url:String) extends Actor {
  // Internal operation
  case class SendRequest()


  // Context for pipeline
  import SprayJsonSupport._
  import ElasticJsonProtocol._
  import context.dispatcher

  val pipeline = sendReceive ~> unmarshal[T]

  context.system.scheduler.scheduleOnce(1.second, self, SendRequest)

  def receive = {
    case SendRequest =>
      val f = pipeline { Get(url) }
      f onComplete {
        case Success(data: T) => println("")
        case Success(rawData) => println("")
        case Failure(error) => println("Failed to get url")
      }

  }
}
