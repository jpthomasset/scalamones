package com.frenchcoder.scalamones.service

import akka.actor.{PoisonPill, Props, ActorRef, Actor}
import com.frenchcoder.scalamones.service.Manager.{UnMonitorServerListChange, MonitorServerListChange, ListServer}
import Manager._

object Manager {
  val props = Props[Manager]()
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
}

class Manager extends Actor {

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
          sender ! ServerRemoved(s.server)
          s.services foreach { case (k, v) => v ! PoisonPill}
          servers = servers filterKeys(_ != serverId)
          broadcastServerListChange()

        case None =>
          sender ! NoSuchServer(serverId)
      }

    case MonitorServerListChange => serverListener += sender
    case UnMonitorServerListChange => serverListener -= sender
  }

  def broadcastServerListChange() = {
    serverListener foreach(_ ! ServerList((servers map { case (k, v) => v.server }).toSet))
  }
}
