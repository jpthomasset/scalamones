package com.frenchcoder.scalamones

import com.frenchcoder.scalamones.service.KpiProvider
import spray.httpx.unmarshalling._

import scala.util.{Success, Failure}
import scala.concurrent.duration._
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.event.Logging
import akka.io.IO
import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.can.Http
import spray.client.pipelining._
import spray.util._
import elastic.Stat._
import elastic.ElasticJsonProtocol
import spray.httpx.SprayJsonSupport

/**
 *
 */
object Main extends App {
  implicit val system = ActorSystem("simple-spray-client")
  import system.dispatcher
  val log = Logging(system, getClass)

  log.info("Requesting stat api...")
  import SprayJsonSupport._
  import ElasticJsonProtocol._
  //val test = system.actorOf(KpiProvider.nodeStatProps[NodeJvmStat](_.jvm)("http://localhost:9200"))
  //val test = system.actorOf(Props(new KpiProvider[NodesStat, NodesStat]("http://localhost:9200/_nodes/stats", n => n)))
  def nodeStatProps[T: FromResponseUnmarshaller](e: NodesStat => T) =
    Props(new KpiProvider[NodesStat, T]("http://localhost:9200/_nodes/stats/jvm", e))
  //val test2 = system.actorOf(Props(new KpiProvider[NodesStat, Option[NodeJvmStat]]("http://localhost:9200/_nodes/stats/jvm", n=> n.nodes.head._2.jvm )))
  val test3 = system.actorOf(nodeStatProps[Map[String, Option[NodeJvmStat]]](n => n.nodes map ( m => (m._1, m._2.jvm))))
    //.props[NodesStat]("http://localhost:9200/_nodes/stats"))

/*
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
  }*/
}