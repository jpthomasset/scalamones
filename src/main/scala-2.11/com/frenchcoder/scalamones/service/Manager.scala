package com.frenchcoder.scalamones.service

import akka.actor._
import com.frenchcoder.scalamones.elastic.Stat.ElasticKpi
import com.frenchcoder.scalamones.service.KpiProvider.{KpiUnMonitor, KpiMonitor}
import com.frenchcoder.scalamones.service.Manager._
import Manager._
import spray.client.pipelining._
import scala.concurrent.ExecutionContext
import scala.reflect._

object Manager {
  def props(implicit refFactory: ActorRefFactory, executionContext: ExecutionContext) :Props =  {
    implicit val s = sendReceive
    Props(new Manager)
  }
  /**
   * Message to add a server
   * @param host The host of the server to monitor
   * @param port The port of the server to monitorj */
  case class AddServer(host: String, port: Int)
  case class ListServer()
  case class RemoveServer(serverId: Int)

  case class ServerList(servers: Set[Server])
  case class ServerAdded(server: Server)
  case class ServerRemoved(server: Server)
  case class NoSuchServer(serverId: Int)

  case class MonitorServerListChange()
  case class UnMonitorServerListChange()

  case class Monitor(serverId: Int, kpiClass:String)
  case class UnMonitor(serverId: Int, kpiClass:String)
  case class NoSuchKpiProvider(kpiClass: String)


  object Monitor {
    def apply[T](serverId:Int)(implicit tag:ClassTag[T]): Monitor = new Monitor(serverId, tag.toString())
  }

  object UnMonitor {
    def apply[T](serverId:Int)(implicit tag:ClassTag[T]): UnMonitor = new UnMonitor(serverId, tag.toString())
  }
}

class Manager(implicit s:SendReceive) extends Actor {

  import context._

  var _seqServerId = 0
  def nextServerId = {
    _seqServerId += 1
    _seqServerId
  }

  case class ServerContext(server: Server, services: Map[String, ActorRef])

  var servers = Map.empty[Int, ServerContext]
  var serverListener = Set.empty[ActorRef]

  def receive = {
    case AddServer(host, port) =>
      val serverService = KpiProvider.startServices("http://" + host + ":" + port)
      val serverContext = ServerContext(Server(nextServerId, host, port), serverService)

      // Store services for future usage
      servers += (serverContext.server.id -> serverContext)
      sender ! ServerAdded(serverContext.server)
      broadcastServerListChange()

    case ListServer => sender ! ServerList((servers map { case (k, v) => v.server }).toSet)

    case RemoveServer(serverId) =>

      servers.get(serverId) match {
        case Some(s) =>
          s.services foreach { case (k, v) => v ! PoisonPill}
          servers = servers filterKeys(_ != serverId)
          sender ! ServerRemoved(s.server)
          broadcastServerListChange()

        case None =>
          sender ! NoSuchServer(serverId)
      }

    case MonitorServerListChange => serverListener += sender
    case UnMonitorServerListChange => serverListener -= sender

    case Monitor(serverId, tag) =>
      forwardMonitoringRequest(serverId, tag, KpiMonitor(sender))

    case UnMonitor(serverId, tag) =>
      forwardMonitoringRequest(serverId, tag, KpiUnMonitor(sender))


  }

  def forwardMonitoringRequest(serverId:Int, tag:String, message: Any): Unit = {
    servers.get(serverId) match {
      case Some(s) =>
        // Find corresponding KpiProvider
        s.services.get(tag) match {
          case Some(a) => a ! message
          case None => sender ! NoSuchKpiProvider(tag)
        }

      case None => sender ! NoSuchServer(serverId)
    }
  }

  def broadcastServerListChange() = {
    serverListener foreach(_ ! ServerList((servers map { case (k, v) => v.server }).toSet))
  }
}
