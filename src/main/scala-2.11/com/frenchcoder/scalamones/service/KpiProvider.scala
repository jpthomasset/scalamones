package com.frenchcoder.scalamones.service

import akka.actor._
import akka.event.Logging
import com.frenchcoder.scalamones.elastic.ElasticJsonProtocol
import com.frenchcoder.scalamones.elastic.Stat._
import com.frenchcoder.scalamones.service.KpiProvider.{Notify, UnMonitor, Monitor}
import spray.can.Http
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.httpx.unmarshalling._
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.reflect._


object KpiProvider {

  case class Monitor(watcher: ActorRef)
  case class UnMonitor(watcher: ActorRef)
  case class Notify[T](kpi:T)


  import SprayJsonSupport._
  import ElasticJsonProtocol._

  def startServices(baseUrl: String)(implicit c: ActorContext) : Map[String, ActorRef] = {
    serviceMap map { case (key, value) => (key, c.actorOf(value(baseUrl))) }
  }

  private[service]
  def nodeStatProps[T: JsonFormat](e: NodeStat => Option[T], path:String)(baseUrl: String): Props =
    Props(new KpiProvider[NodesStat, Map[String, Option[T]]](baseUrl + path, (n => n.nodes map ( m => (m._1, e(m._2)))) ))

  private[service]
  val serviceMap: Map[String, (String => Props)] = Map(
    classTag[NodeJvmStat].toString() -> nodeStatProps[NodeJvmStat](_.jvm, "/_nodes/stats/jvm"),
    classTag[NodeOsStat].toString() -> nodeStatProps[NodeOsStat](_.os, "/_nodes/stats/os")
  )

}

class KpiProvider[T: FromResponseUnmarshaller, U: FromResponseUnmarshaller](val url:String, val extractor: T=>U) extends Actor {

  val log = Logging(context.system, getClass)

  // Remove import context._ to prevent ambiguous implicit ActorRefFactory in context & system
  import context.dispatcher
  import context.become
  // Internal operation
  case class SendRequest()



  val pipeline = sendReceive ~> unmarshal[T]
  var watchers = Set.empty[ActorRef]
  var latestValue: Option[U] = None

  //self ! SendRequest
  def receive = idle

  def idle: Receive = {
    case Monitor(watcher) =>
      log.debug("Monitor message received, become active")
      watchers += watcher
      // Schedule update every 5 seconds
      val scheduledRequestor = context.system.scheduler.schedule(0.seconds, 5.seconds, self, SendRequest)
      become(active(scheduledRequestor))
  }

  def active(scheduledRequestor: Cancellable): Receive = {
    case Monitor(watcher) =>
      log.debug("Monitor message received, already active")
      watchers += watcher
      // Send latest value to watcher so it gets immediately a value
      latestValue foreach (watcher ! Notify(_))

    case UnMonitor(watcher) =>
      log.debug("UnMonitor message received")
      watchers -= watcher
      if(watchers.isEmpty) {
        log.debug("No more watchers, become idle")
        scheduledRequestor.cancel()
        latestValue = None
        become(idle)
      }

    case SendRequest =>
      val f = pipeline { Get(url) }
      f onComplete {
        case Success(data) =>
          log.debug(s"Received new data from ${url}")
          if(data.isInstanceOf[T]) {
            val embed = extractor(data.asInstanceOf[T])
            if(embed.isInstanceOf[U]) {
              latestValue = Some(embed)
              // Notify watchers of new value
              watchers foreach( _ ! Notify(embed))
            }
          }

        case Failure(error) => log.error(error, s"Error while fetching data from ${url}")
      }

  }
}
