package com.frenchcoder.scalamones.service

import akka.actor.{Props, Actor}
import com.frenchcoder.scalamones.elastic.ElasticJsonProtocol
import com.frenchcoder.scalamones.elastic.Stat._
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.httpx.unmarshalling._
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.reflect._


object KpiProvider {
  def props[T: FromResponseUnmarshaller](url: String) = Props(new KpiProvider[T](url))

  case class KpiReceived[T](kpi: T)
}

class KpiProvider[T: FromResponseUnmarshaller](val url:String) extends Actor {
  // Internal operation
  case class SendRequest()

  // Import context
  import context.dispatcher

  val pipeline = sendReceive ~> unmarshal[T]
  val tclass = classTag[T]
  context.system.scheduler.scheduleOnce(1.second, self, SendRequest)

  def receive = {
    case SendRequest =>
      val f = pipeline { Get(url) }
      f onComplete {
        case Success(data) =>
          /*val t = data.asInstanceOf[T]
          println("Ok")*/
          if(data.isInstanceOf[T]) {
            println("Ok")
          } else {
            println("KO !!")
          }
        case Failure(error) => println("Failed to get url")
      }

  }
}
