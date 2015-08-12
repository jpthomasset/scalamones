package service

import akka.actor.{ActorRef, Actor}


class Manager extends Actor {

  /**
   * Message to add a server
   * @param host The host of the server to monitor
   * @param port The port of the server to monitor
   */
  case class AddServer(host: String, port: Int)
  case class ListServer()
  case class ServerList(servers: Set[Server])
  case class RemoveServer(server: Server)

  case class MonitorServerListChange()
  case class UnMonitorServerListChange()


  var _seqServerId = 0
  def nextServerId = {
    _seqServerId += 1
    _seqServerId
  }

  var servers = Set.empty[Server]
  var serverListener = Set.empty[ActorRef]

  def receive = {
    case AddServer(host, port) =>
      servers += Server(nextServerId, host, port)
      // Todo Add service for this new host
      broadcastServerListChange()

    case ListServer => sender ! ServerList(servers)

    case RemoveServer(server) =>
      servers -= server
      // Todo Stop service for this new host
      broadcastServerListChange()

    case MonitorServerListChange => serverListener += sender
    case UnMonitorServerListChange => serverListener -= sender
  }

  def broadcastServerListChange() = {
    serverListener foreach(_ ! servers)
  }
}
