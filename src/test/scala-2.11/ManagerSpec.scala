import akka.actor.ActorSystem
import akka.testkit.{ TestKit, ImplicitSender }
import com.frenchcoder.scalamones.service.Manager
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
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
      manager ! AddServer("es.example.com", 1234)

      val addedMsg = expectMsgType[ServerAdded]
      addedMsg.server.host should be("es.example.com")
      addedMsg.server.port should be(1234)
    }

    "ack requester when server removed" in {
      val manager = system.actorOf(Manager.props)

      manager ! AddServer("es.example.com", 1234)
      val addedMsg = expectMsgType[ServerAdded]

      manager ! RemoveServer(addedMsg.server.id)

      val removedMessage = expectMsgType[ServerRemoved]
      removedMessage.server.id should be(addedMsg.server.id)

    }

    "notify ServerList listener when server added" in {
      val manager = system.actorOf(Manager.props)
      manager ! MonitorServerListChange

      manager ! AddServer("es.example.com", 1234)

      expectMsgType[ServerAdded]

      val result1 = expectMsgType[ServerList].servers filter( s => s.host eq "es.example.com")
      result1.size should be(1)

    }

  }
}