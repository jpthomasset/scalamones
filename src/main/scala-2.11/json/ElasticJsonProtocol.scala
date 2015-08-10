package json

import spray.json.DefaultJsonProtocol

case class NodeOsCpuStat(
                          sys: Int,
                          user:Int,
                          idle: Int,
                          usage:Int,
                          stolen:Int
                          )

case class NodeOsMemStat(
                          free_in_bytes: Long,
                          used_in_bytes: Long,
                          free_percent: Int,
                          used_percent: Int,
                          actual_free_in_bytes: Long,
                          actual_used_in_bytes: Long
                          )

case class NodeOsSwapStat(
                           free_in_bytes: Long,
                           used_in_bytes: Long
                           )
case class NodeOsStat(
                       timestamp: Long,
                       uptime_in_millis: Long,
                       load_average: Array[Double],
                       cpu: NodeOsCpuStat,
                       mem: NodeOsMemStat,
                       swap: NodeOsSwapStat
                       )

case class NodeProcessCpuStat(
                               percent: Int,
                               sys_in_millis: Long,
                               user_in_millis: Long,
                               total_in_millis: Long
                               )

case class NodeProcessMemStat(
                               resident_in_bytes: Long,
                               share_in_bytes: Long,
                               total_virtual_in_bytes: Long
                               )

case class NodeProcessStat(
                            timestamp: Long,
                            open_file_descriptors: Int,
                            cpu: NodeProcessCpuStat,
                            mem: NodeProcessMemStat
                            )

case class NodeStat(
                     timestamp: Long,
                     name: String,
                     transport_address: String,
                     host: String,
                     ip: Array[String],
                     indices: Map[String, String],
                     os: NodeOsStat,
                     process: NodeProcessStat,
                     jvm: Map[String, String],
                     thread_pool: Map[String, String],
                     network: Map[String, String],
                     fs: Map[String, String],
                     transport: Map[String, String],
                     http: Map[String, String],
                     breakers: Map[String, String]
                     )

case class ClusterStat(cluster_name: String, nodes: Map[String, NodeStat])



object ElasticJsonProtocol extends DefaultJsonProtocol {
  implicit val nodeStatFormat = jsonFormat15(NodeStat)
  implicit val clusterStatFormat = jsonFormat2(ClusterStat)
}
