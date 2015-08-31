package com.frenchcoder.scalamones.service

import akka.actor._
import akka.event.Logging
import com.frenchcoder.scalamones.elastic.ClusterStat._
import com.frenchcoder.scalamones.elastic.ElasticJsonProtocol
import com.frenchcoder.scalamones.elastic.Stat._
import com.frenchcoder.scalamones.service.KpiProvider.{KpiNotify, KpiUnMonitor, KpiMonitor}
import spray.client.pipelining._
import spray.http.Uri
import spray.httpx.SprayJsonSupport
import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.httpx.unmarshalling._
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.reflect._


object KpiProvider {

  case class KpiMonitor(watcher: ActorRef)
  case class KpiUnMonitor(watcher: ActorRef)
  case class KpiNotify[T](kpi:T)


  import SprayJsonSupport._
  import ElasticJsonProtocol._

  def startServices(baseUrl: Uri)(implicit c: ActorContext, s:SendReceive) : Map[String, ActorRef] = {
    serviceMap map { case (key, value) => (key, c.actorOf(value(s, baseUrl))) }
  }

  private[service]
  def nodeStatProps[T: JsonFormat](e: NodeStat => Option[T], path:String)(s:SendReceive, baseUrl: Uri): Props =
    Props(new KpiProvider[NodesStat, Map[String, Option[T]]](s, baseUrl + path, (n => n.nodes map ( m => (m._1, e(m._2)))) ))

  def noEnvelopeStatProps[T: FromResponseUnmarshaller](path: String)(s:SendReceive, baseUrl: Uri): Props =
    Props(new KpiProvider[T, T](s, baseUrl + path, (n => n) ))

  import ClusterStatProtocol._
  private[service]
  val serviceMap: Map[String, ((SendReceive, Uri) => Props)] = Map(
    classTag[NodeJvmStat].toString() -> nodeStatProps[NodeJvmStat](_.jvm, "/_nodes/stats/jvm"),
    classTag[NodeOsStat].toString() -> nodeStatProps[NodeOsStat](_.os, "/_nodes/stats/os"),
    classTag[ClusterHealth].toString() -> noEnvelopeStatProps[ClusterHealth]("/_cluster/health"),
    classTag[ClusterStat].toString() -> noEnvelopeStatProps[ClusterStat]("/_cluster/stats")
  )

}

class KpiProvider[T: FromResponseUnmarshaller, U: FromResponseUnmarshaller](val s:SendReceive, val url:String, val extractor: T=>U) extends Actor {

  val log = Logging(context.system, getClass)

  // Remove import context._ to prevent ambiguous implicit ActorRefFactory in context & system
  import context.dispatcher
  import context.become
  // Internal operation
  case class SendRequest()
  case class KpiError(error: Throwable)


  val pipeline = s ~> unmarshal[T]
  var watchers = Set.empty[ActorRef]
  var latestValue: Option[U] = None

  //self ! SendRequest
  def receive = idle

  def idle: Receive = {
    case KpiMonitor(watcher) =>
      log.debug("Monitor message received, become active")
      watchers += watcher
      // Schedule update every 5 seconds
      val scheduledRequestor = context.system.scheduler.schedule(0.seconds, 5.seconds, self, SendRequest)
      become(active(scheduledRequestor) orElse waitingForRequest(scheduledRequestor))
  }

  def active(scheduledRequestor: Cancellable): Receive = {
    case KpiMonitor(watcher) =>
      log.debug("Monitor message received, already active")
      watchers += watcher
      // Send latest value to watcher so it gets immediately a value
      latestValue foreach (watcher ! KpiNotify(_))

    case KpiUnMonitor(watcher) =>
      log.debug("UnMonitor message received")
      watchers -= watcher
      if (watchers.isEmpty) {
        log.debug("No more watchers, become idle")
        scheduledRequestor.cancel()
        latestValue = None
        become(idle)
      }
    case KpiError(error) =>
      // Todo Forward error to manager ?
      become(active(scheduledRequestor) orElse waitingForRequest(scheduledRequestor))

  }

  def waitingForRequest(scheduledRequestor: Cancellable): Receive = {

    case SendRequest =>
      val f = pipeline {
        Get(url)
      }
      f onComplete {
        case Success(data) =>
          log.debug(s"Received new data from ${url}")
          if (data.isInstanceOf[T]) {
            val embed = extractor(data.asInstanceOf[T])
            if (embed.isInstanceOf[U]) {
              // Notify self of new value
              self ! KpiNotify(embed)
            }
          }

        case Failure(error) =>
          self ! KpiError(error)
          log.error(error, s"Error while fetching data from ${url}")
      }
      become(active(scheduledRequestor) orElse waitingForResponse(scheduledRequestor))
  }

  def waitingForResponse(scheduledRequestor: Cancellable): Receive = {

    case KpiNotify(data) =>
      latestValue = Some(data.asInstanceOf[U])
      watchers foreach( _ ! KpiNotify(data))
      become(active(scheduledRequestor) orElse waitingForRequest(scheduledRequestor))
  }
}
