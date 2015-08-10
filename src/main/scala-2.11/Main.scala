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

  import ElasticJsonProtocol._
  import SprayJsonSupport._


  val pipeline = sendReceive ~> unmarshal[ClusterStat]

  val responseFuture = pipeline {
    Get("http://localhost:9200/_nodes/stats")
  }

  responseFuture onComplete {
    case Success(stat: ClusterStat) =>
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