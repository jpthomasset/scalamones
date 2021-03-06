import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.frenchcoder.scalamones.elastic.ElasticJsonProtocol
import com.frenchcoder.scalamones.elastic.Stat.NodesStat
import com.frenchcoder.scalamones.service.KpiProvider
import com.frenchcoder.scalamones.service.KpiProvider.{KpiNotify, KpiMonitor}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import spray.http._
import spray.httpx.SprayJsonSupport
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class KpiProviderSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("ManagerSpec"))
  val emptyNodeStatJson = "{\"cluster_name\":\"elasticsearch\",\"nodes\":{\"5thWy2-dRCynqF7J4_Cpqw\":{\"timestamp\":1439738312505,\"name\":\"Harmonica\",\"transport_address\":\"inet[/172.17.0.1:9300]\",\"host\":\"1bf4f8e0cc6c\",\"ip\":[\"inet[/172.17.0.1:9300]\",\"NONE\"]}}}"
  val otherNodeStatJson = "{\"cluster_name\":\"other\",\"nodes\":{\"5thWy2-dRCynqF7J4_Cpqw\":{\"timestamp\":1439738312505,\"name\":\"Harmonica\",\"transport_address\":\"inet[/172.17.0.1:9300]\",\"host\":\"1bf4f8e0cc6c\",\"ip\":[\"inet[/172.17.0.1:9300]\",\"NONE\"]}}}"
  var requestCount = 0;
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  def sendAndReceive(response:String)(request: HttpRequest) : Future[HttpResponse] = {
    Future(HttpResponse(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, response.getBytes())))
  }

  def blockingSendAndReceive(response:String)(request: HttpRequest): Future[HttpResponse] = {
    requestCount = requestCount + 1
    akka.pattern.after[HttpResponse](1.seconds, using = system.scheduler)(
      Future(HttpResponse(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, response.getBytes())))
    )
  }

  "A KpiProvider" must {
    "respond to monitor request" in  {
      import SprayJsonSupport._
      import ElasticJsonProtocol._

      val provider = system.actorOf(Props(new KpiProvider[NodesStat, NodesStat](sendAndReceive(emptyNodeStatJson), "dummyUrl", (n => n))))
      provider ! KpiMonitor(self)
      val nodeValue = expectMsgType[KpiNotify[NodesStat]]
    }

    "not send a request while there is another one pending" in  {
      import SprayJsonSupport._
      import ElasticJsonProtocol._

      // Count request
      val provider = system.actorOf(Props(new KpiProvider[NodesStat, NodesStat](blockingSendAndReceive(otherNodeStatJson), "dummyUrl", (n => n))))
      provider ! KpiMonitor(self)
      provider ! KpiMonitor(self)
      val nodeValue = expectMsgType[KpiNotify[NodesStat]]
      requestCount should be (1)
    }
  }

}
