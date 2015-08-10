import scala.util.{Success, Failure}
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.event.Logging
import akka.io.IO
import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.can.Http
import spray.httpx.SprayJsonSupport
import spray.client.pipelining._
import spray.util._


/**
 *
 */
object Main extends App {
  implicit val system = ActorSystem("simple-spray-client")
  import system.dispatcher
  val log = Logging(system, getClass)

  log.info("Requesting the elevation of Mt. Everest from Googles Elevation API...")

  // execution context for futures below

  import json._

  object ElasticJsonProtocol extends DefaultJsonProtocol {
    implicit val nodeOsCpuStatFormat = jsonFormat5(NodeOsCpuStat)
    implicit val nodeOsMemStatFormat = jsonFormat6(NodeOsMemStat)
    implicit val nodeOsSwapStatFormat = jsonFormat2(NodeOsSwapStat)
    implicit val nodeOsStatFormat = jsonFormat6(NodeOsStat)
    implicit val nodeProcessCpuStatFormat = jsonFormat4(NodeProcessCpuStat)
    implicit val nodeProcessMemStatFormat = jsonFormat3(NodeProcessMemStat)
    implicit val nodeProcessStatFormat = jsonFormat4(NodeProcessStat)
    implicit val nodeJvmMemPoolStatFormat = jsonFormat4(NodeJvmMemPoolStat)
    implicit val nodeJvmMemStatFormat = jsonFormat7(NodeJvmMemStat)
    implicit val nodeJvmThreadsStatFormat = jsonFormat2(NodeJvmThreadsStat)
    implicit val nodeJvmGcCollectorStatFormat = jsonFormat2(NodeJvmGcCollectorStat)
    implicit val nodeJvmGcStatFormat = jsonFormat1(NodeJvmGcStat)
    implicit val nodeJvmBufferPoolStatFormat = jsonFormat3(NodeJvmBufferPoolStat)
    implicit val nodeThreadPoolStatFormat = jsonFormat6(NodeThreadPoolStat)
    implicit val nodeJvmStatFormat = jsonFormat11(NodeJvmStat)
    implicit val nodeNetworkStatFormat = jsonFormat10(NodeNetworkStat)
    implicit val nodeFsTotalStatFormat = jsonFormat3(NodeFsTotalStat)
    implicit val nodeFsDataStatFormat = jsonFormat6(NodeFsDataStat)
    implicit val nodeFsStatFormat = jsonFormat3(NodeFsStat)
    implicit val nodeTransportStatFormat = jsonFormat5(NodeTransportStat)
    implicit val nodeHttpStatFormat = jsonFormat2(NodeHttpStat)
    implicit val nodeBreakerStatFormat = jsonFormat6(NodeBreakerStat)
    implicit val nodeStatFormat = jsonFormat15(NodeStat)
    implicit val nodesStatFormat = jsonFormat2(NodesStat)
    implicit val clusterHealthFormat = jsonFormat10(ClusterHealth)
  }



  val pipeline = sendReceive ~> unmarshal[NodesStat]

  val responseFuture = pipeline {
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
      log.warning("Sucess with raw response.", somethingUnexpected)
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