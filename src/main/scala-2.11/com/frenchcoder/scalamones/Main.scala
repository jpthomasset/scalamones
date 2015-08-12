package com.frenchcoder.scalamones

import akka.actor.ActorSystem
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.client.pipelining._
import spray.util._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 *
 */
object Main extends App {
  implicit val system = ActorSystem("simple-spray-client")
  import system.dispatcher
  val log = Logging(system, getClass)

  log.info("Requesting stat api...")

  // Context for futures below

  val pipeline = sendReceive ~> unmarshal[NodesStat]

  val responseFuture = pipeline {
    // http://31.172.161.21:9200/_nodes/stats
    // http://localhost:9200/_nodes/stats
    Get("http://localhost:9200/_nodes/stats")
  }

  responseFuture onComplete {
    case Success(stat: NodesStat) =>
      log.info("Found cluster name '{}'", stat.cluster_name)
      stat.nodes.foreach { case (nodeName: String, nodeStat:NodeStat) =>
        log.info(" | -> Node '{}'", nodeStat.name)
      }

      shutdown()

    case Success(somethingUnexpected) =>
      log.warning("Success with raw response.", somethingUnexpected)
      shutdown()

    case Failure(error) =>
      log.error(error, "Error")
      shutdown()
  }

  def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }
}