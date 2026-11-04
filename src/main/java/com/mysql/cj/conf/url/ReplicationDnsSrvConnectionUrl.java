package com.mysql.cj.conf.url;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.BooleanPropertyDefinition;
import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.conf.ConnectionUrlParser;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.HostsListView;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ReplicationDnsSrvConnectionUrl extends ConnectionUrl {
   private static final String DEFAULT_HOST = "";
   private static final int DEFAULT_PORT = -1;
   private static final String TYPE_SOURCE = "SOURCE";
   private static final String TYPE_REPLICA = "REPLICA";
   @Deprecated
   private static final String TYPE_SOURCE_DEPRECATED = "MASTER";
   @Deprecated
   private static final String TYPE_REPLICA_DEPRECATED = "SLAVE";
   private List<HostInfo> sourceHosts = new ArrayList<>();
   private List<HostInfo> replicaHosts = new ArrayList<>();

   public ReplicationDnsSrvConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      super(connStrParser, info);
      this.type = ConnectionUrl.Type.REPLICATION_DNS_SRV_CONNECTION;
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

      HostInfo srvHostSource = this.sourceHosts.isEmpty() ? null : this.sourceHosts.get(0);
      Map<String, String> hostPropsSource = srvHostSource == null ? Collections.emptyMap() : srvHostSource.getHostProperties();
      HostInfo srvHostReplica = this.replicaHosts.isEmpty() ? null : this.replicaHosts.get(0);
      Map<String, String> hostPropsReplica = srvHostReplica == null ? Collections.emptyMap() : srvHostReplica.getHostProperties();
      if (srvHostSource == null || srvHostReplica == null || "".equals(srvHostSource.getHost()) || "".equals(srvHostReplica.getHost())) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(
            InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.20")
         );
      } else if (this.sourceHosts.size() != 1 || this.replicaHosts.size() != 1) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(
            InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.21")
         );
      } else if (srvHostSource.getPort() != -1 || srvHostReplica.getPort() != -1) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(
            InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.22")
         );
      } else if ((hostPropsSource.containsKey(PropertyKey.dnsSrv.getKeyName()) || hostPropsReplica.containsKey(PropertyKey.dnsSrv.getKeyName()))
         && (
            !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), hostPropsSource.get(PropertyKey.dnsSrv.getKeyName()), null)
               || !BooleanPropertyDefinition.booleanFrom(PropertyKey.dnsSrv.getKeyName(), hostPropsReplica.get(PropertyKey.dnsSrv.getKeyName()), null)
         )) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(
            InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.23", new Object[]{PropertyKey.dnsSrv.getKeyName()})
         );
      } else if (hostPropsSource.containsKey(PropertyKey.PROTOCOL.getKeyName())
            && hostPropsSource.get(PropertyKey.PROTOCOL.getKeyName()).equalsIgnoreCase("PIPE")
         || hostPropsReplica.containsKey(PropertyKey.PROTOCOL.getKeyName()) && hostPropsReplica.get(PropertyKey.PROTOCOL.getKeyName()).equalsIgnoreCase("PIPE")
         )
       {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(
            InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.24")
         );
      } else if (hostPropsSource.containsKey(PropertyKey.replicationConnectionGroup.getKeyName())
         || hostPropsReplica.containsKey(PropertyKey.replicationConnectionGroup.getKeyName())) {
         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(
            InvalidConnectionAttributeException.class,
            Messages.getString("ConnectionString.25", new Object[]{PropertyKey.replicationConnectionGroup.getKeyName()})
         );
      }
   }

   @Override
   public String getDefaultHost() {
      return "";
   }

   @Override
   public int getDefaultPort() {
      return -1;
   }

   @Override
   public List<HostInfo> getHostsList(HostsListView view) {
      switch (view) {
         case SOURCES:
            return this.getHostsListFromDnsSrv(this.sourceHosts.get(0));
         case REPLICAS:
            return this.getHostsListFromDnsSrv(this.replicaHosts.get(0));
         default:
            return super.getHostsList(HostsListView.ALL);
      }
   }
}
