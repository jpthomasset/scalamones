import akka.actor.ActorSystem
import akka.testkit.{ TestKit, ImplicitSender }
import com.frenchcoder.scalamones.service.Manager
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import spray.http.Uri
import scala.concurrent.ExecutionContext.Implicits.global
import Manager._

class ManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("ManagerSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Manager actor" must {

    "ack requester when server added" in {
      val manager = system.actorOf(Manager.props)
      manager ! AddServer("http://es.example.com:1234")

      val addedMsg = expectMsgType[ServerAdded]
      addedMsg.server.url should be(Uri("http://es.example.com:1234"))
    }

    "ack requester when server removed" in {
      val manager = system.actorOf(Manager.props)

      manager ! AddServer("http://es.example.com:1234")
      val addedMsg = expectMsgType[ServerAdded]

      manager ! RemoveServer(addedMsg.server.id)

      val removedMessage = expectMsgType[ServerRemoved]
      removedMessage.server.id should be(addedMsg.server.id)

    }

    "notify ServerList listener when server added" in {
      val manager = system.actorOf(Manager.props)
      manager ! MonitorServerListChange

      manager ! AddServer("http://es.example.com:1234")

      expectMsgType[ServerAdded]

      val result1 = expectMsgType[ServerList].servers filter( s => s.url eq Uri("http://es.example.com:1234"))
      result1.size should be(1)

    }

  }
}