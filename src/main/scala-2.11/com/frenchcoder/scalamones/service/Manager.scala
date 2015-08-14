package com.frenchcoder.scalamones.service

import akka.actor.{Props, ActorRef, Actor}
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

  var servers = Set.empty[Server]
  var serverListener = Set.empty[ActorRef]

  def receive = {
    case AddServer(host, port) =>
      val server = Server(nextServerId, host, port)
      servers += server
      sender ! ServerAdded(server)
      // Todo Add service for this new host
      KpiProvider.startServices(context, "http://" + host + ":" + port)
      broadcastServerListChange()

    case ListServer => sender ! ServerList(servers)

    case RemoveServer(serverId) =>
      val deletedServers = servers filter ( s => s.id == serverId)
      if(deletedServers.size == 0) {
        sender ! NoSuchServer(serverId)
      }
      servers -= deletedServers.head
      sender ! ServerRemoved(deletedServers.head)
      // Todo Stop service for this new host
      broadcastServerListChange()

    case MonitorServerListChange => serverListener += sender
    case UnMonitorServerListChange => serverListener -= sender
  }

  def broadcastServerListChange() = {
    serverListener foreach(_ ! ServerList(servers))
  }
}
