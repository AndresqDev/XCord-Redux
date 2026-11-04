package com.mysql.cj.conf.url;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ReplicationConnectionUrl extends ConnectionUrl {
   private static final String TYPE_SOURCE = "SOURCE";
   private static final String TYPE_REPLICA = "REPLICA";
   @Deprecated
   private static final String TYPE_SOURCE_DEPRECATED = "MASTER";
   @Deprecated
   private static final String TYPE_REPLICA_DEPRECATED = "SLAVE";
   private List<HostInfo> sourceHosts = new ArrayList<>();
   private List<HostInfo> replicaHosts = new ArrayList<>();

   public ReplicationConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
      LinkedList<HostInfo> undefinedHosts = new LinkedList<>();

      for (HostInfo hi : this.hosts) {
         Map<String, String> hostProperties = hi.getHostProperties();
         if (hostProperties.containsKey(PropertyKey.TYPE.getKeyName())) {
            if ("SOURCE".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))
               || "MASTER".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
               this.sourceHosts.add(hi);
            } else if (!"REPLICA".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))
               && !"SLAVE".equalsIgnoreCase(hostProperties.get(PropertyKey.TYPE.getKeyName()))) {
               undefinedHosts.add(hi);
            } else {
               this.replicaHosts.add(hi);
            }
         } else {
            undefinedHosts.add(hi);
         }
      }

      if (!undefinedHosts.isEmpty()) {
         if (this.sourceHosts.isEmpty()) {
            this.sourceHosts.add(undefinedHosts.removeFirst());
         }

         this.replicaHosts.addAll(undefinedHosts);
      }
   }

   public ReplicationConnectionUrl(List<HostInfo> sources, List<HostInfo> replicas, Map<String, String> properties) {
      this.originalConnStr = ConnectionUrl.Type.REPLICATION_CONNECTION.getScheme() + "//**internally_generated**" + System.currentTimeMillis() + "**";
      this.originalDatabase = properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? properties.get(PropertyKey.DBNAME.getKeyName()) : "";
      this.type = ConnectionUrl.Type.REPLICATION_CONNECTION;
      this.properties.putAll(properties);
      this.injectPerTypeProperties(this.properties);
      this.setupPropertiesTransformer();
      sources.stream().map(this::fixHostInfo).peek(this.sourceHosts::add).forEach(this.hosts::add);
      replicas.stream().map(this::fixHostInfo).peek(this.replicaHosts::add).forEach(this.hosts::add);
   }

   @Override
   public List<HostInfo> getHostsList(HostsListView view) {
      switch (view) {
         case SOURCES:
            return Collections.unmodifiableList(this.sourceHosts);
         case REPLICAS:
            return Collections.unmodifiableList(this.replicaHosts);
         default:
            return super.getHostsList(HostsListView.ALL);
      }
   }

   public HostInfo getSourceHostOrSpawnIsolated(String hostPortPair) {
      return super.getHostOrSpawnIsolated(hostPortPair, this.sourceHosts);
   }

   public List<String> getSourcesListAsHostPortPairs() {
      return this.sourceHosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
   }

   public List<HostInfo> getSourceHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
      return hostPortPairs.stream().map(this::getSourceHostOrSpawnIsolated).collect(Collectors.toList());
   }

   public HostInfo getReplicaHostOrSpawnIsolated(String hostPortPair) {
      return super.getHostOrSpawnIsolated(hostPortPair, this.replicaHosts);
   }

   public List<String> getReplicasListAsHostPortPairs() {
      return this.replicaHosts.stream().map(hi -> hi.getHostPortPair()).collect(Collectors.toList());
   }

   public List<HostInfo> getReplicaHostsListFromHostPortPairs(Collection<String> hostPortPairs) {
      return hostPortPairs.stream().map(this::getReplicaHostOrSpawnIsolated).collect(Collectors.toList());
   }
}
