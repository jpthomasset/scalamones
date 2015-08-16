package com.frenchcoder.scalamones.elastic

object Stat {

  sealed trait ElasticKpi

  case class NodeOsCpuStat(
                            sys: Int,
                            user: Int,
                            idle: Int,
                            usage: Int,
                            stolen: Int
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
                         ) extends ElasticKpi

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
                              ) extends ElasticKpi


  case class NodeJvmMemPoolStat(
                                 used_in_bytes: Long,
                                 max_in_bytes: Long,
                                 peak_used_in_bytes: Long,
                                 peak_max_in_bytes: Long
                                 )

  case class NodeJvmMemStat(
                             heap_used_in_bytes: Long,
                             heap_used_percent: Long,
                             heap_committed_in_bytes: Long,
                             heap_max_in_bytes: Long,
                             non_heap_used_in_bytes: Long,
                             non_heap_committed_in_bytes: Long,
                             pools: Map[String, NodeJvmMemPoolStat]
                             )


  case class NodeJvmThreadsStat(
                                 count: Int,
                                 peak_count: Int
                                 )

  case class NodeJvmGcCollectorStat(
                                     collection_count: Int,
                                     collection_time_in_millis: Long
                                     )

  case class NodeJvmGcStat(
                            collectors: Map[String, NodeJvmGcCollectorStat]
                            )

  case class NodeJvmBufferPoolStat(
                                    count: Int,
                                    used_in_bytes: Long,
                                    total_capacity_in_bytes: Long
                                    )

  case class NodeThreadPoolStat(
                                 threads: Int,
                                 queue: Int,
                                 active: Int,
                                 rejected: Int,
                                 largest: Int,
                                 completed: Int
                                 ) extends ElasticKpi

  case class NodeJvmStat(
                          timestamp: Long,
                          uptime_in_millis: Long,
                          mem: NodeJvmMemStat,
                          threads: NodeJvmThreadsStat,
                          gc: NodeJvmGcStat,
                          buffer_pools: Map[String, NodeJvmBufferPoolStat]
                          ) extends ElasticKpi

  case class NodeNetworkStat(
                              active_opens: Long,
                              passive_opens: Long,
                              curr_estab: Long,
                              in_segs: Long,
                              out_segs: Long,
                              retrans_segs: Long,
                              estab_resets: Long,
                              attempt_fails: Long,
                              in_errs: Long,
                              out_rsts: Long
                              ) extends ElasticKpi

  case class NodeFsTotalStat(
                              total_in_bytes: Long,
                              free_in_bytes: Long,
                              available_in_bytes: Long
                              )

  case class NodeFsDataStat(
                             path: String,
                             mount: String,
                             dev: String,
                             total_in_bytes: Long,
                             free_in_bytes: Long,
                             available_in_bytes: Long
                             )

  case class NodeFsStat(
                         timestamp: Long,
                         total: NodeFsTotalStat,
                         data: Array[NodeFsDataStat]
                         ) extends ElasticKpi

  case class NodeTransportStat(
                                server_open: Long,
                                rx_count: Long,
                                rx_size_in_bytes: Long,
                                tx_count: Long,
                                tx_size_in_bytes: Long
                                ) extends ElasticKpi

  case class NodeHttpStat(current_open: Long, total_opened: Long) extends ElasticKpi

  case class NodeBreakerStat(
                              limit_size_in_bytes: Long,
                              limit_size: String,
                              estimated_size_in_bytes: Long,
                              estimated_size: String,
                              overhead: Double,
                              tripped: Int
                              ) extends ElasticKpi

  case class NodeStat(
                       timestamp: Long,
                       name: String,
                       transport_address: String,
                       host: String,
                       ip: Array[String],
                       //          indices: Map[String, Any],
                       os: Option[NodeOsStat],
                       process: Option[NodeProcessStat],
                       jvm: Option[NodeJvmStat],
                       thread_pool: Option[Map[String, NodeThreadPoolStat]],
                       network: Option[Map[String, NodeNetworkStat]],
                       fs: Option[NodeFsStat],
                       transport: Option[NodeTransportStat],
                       http: Option[NodeHttpStat],
                       breakers: Option[Map[String, NodeBreakerStat]]
                       ) extends ElasticKpi


  // http://portal.local-dev.com:9200/_nodes/stats
  case class NodesStat(cluster_name: String, nodes: Map[String, NodeStat])

  // http://portal.local-dev.com:9200/_cluster/health
  case class ClusterHealth(
                            cluster_name: String,
                            status: String,
                            timed_out: Boolean,
                            number_of_nodes: Int,
                            number_of_data_nodes: Int,
                            active_primary_shards: Int,
                            active_shards: Int,
                            relocating_shards: Int,
                            initializing_shards: Int,
                            unassigned_shards: Int
                            )

}