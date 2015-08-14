package com.frenchcoder.scalamones.service

import akka.actor.{Props, Actor}
import com.frenchcoder.scalamones.elastic.ElasticJsonProtocol
import com.frenchcoder.scalamones.elastic.Stat._
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.httpx.unmarshalling._
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.reflect._


object KpiProvider {
  //def props[T: FromResponseUnmarshaller](url: String) = Props(new KpiProvider[T](url))

  //case class KpiReceived[T](kpi: T)

  import SprayJsonSupport._
  import ElasticJsonProtocol._


  def nodeStatProps[T: JsonFormat](e: NodeStat => Option[T])(baseUrl: String): Props =
    Props(new KpiProvider[NodesStat, Map[String, Option[T]]](baseUrl + "/_nodes/stats/jvm", (n => n.nodes map ( m => (m._1, e(m._2)))) ))

  val serviceMap: Map[String, (String => Props)] = Map(
    classTag[NodeJvmStat].toString() -> nodeStatProps[NodeJvmStat](_.jvm),
    classTag[NodeOsStat].toString() -> nodeStatProps[NodeOsStat](_.os)
  )

}

class KpiProvider[T: FromResponseUnmarshaller, U: FromResponseUnmarshaller](val url:String, val extractor: T=>U) extends Actor {
  // Internal operation
  case class SendRequest()

  // Import context
  import context.dispatcher

  val pipeline = sendReceive ~> unmarshal[T]
  //val tclass = classTag[T]
  context.system.scheduler.scheduleOnce(1.second, self, SendRequest)

  def receive = {
    case SendRequest =>
      val f = pipeline { Get(url) }
      f onComplete {
        case Success(data) =>
          /*val t = data.asInstanceOf[T]
          println("Ok")*/
          if(data.isInstanceOf[T]) {
            val embed = extractor(data.asInstanceOf[T])
            if(embed.isInstanceOf[U]) {
              println("Ok embed")
            } else {
              println("KO embed !!")
            }
          } else {
            println("KO !!")
          }
        case Failure(error) => println("Failed to get url " + error)
      }

  }
}
