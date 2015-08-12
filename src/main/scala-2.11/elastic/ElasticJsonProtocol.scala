package elastic

import Stat._
import spray.json.DefaultJsonProtocol

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
  implicit val nodeJvmStatFormat = jsonFormat6(NodeJvmStat)
  implicit val nodeNetworkStatFormat = jsonFormat10(NodeNetworkStat)
  implicit val nodeFsTotalStatFormat = jsonFormat3(NodeFsTotalStat)
  implicit val nodeFsDataStatFormat = jsonFormat6(NodeFsDataStat)
  implicit val nodeFsStatFormat = jsonFormat3(NodeFsStat)
  implicit val nodeTransportStatFormat = jsonFormat5(NodeTransportStat)
  implicit val nodeHttpStatFormat = jsonFormat2(NodeHttpStat)
  implicit val nodeBreakerStatFormat = jsonFormat6(NodeBreakerStat)
  implicit val nodeStatFormat = jsonFormat14(NodeStat)
  implicit val nodesStatFormat = jsonFormat2(NodesStat)
  implicit val clusterHealthFormat = jsonFormat10(ClusterHealth)
}
