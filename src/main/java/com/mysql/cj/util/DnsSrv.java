package com.mysql.cj.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class DnsSrv {
   public static List<DnsSrv.SrvRecord> lookupSrvRecords(String serviceName) throws NamingException {
      List<DnsSrv.SrvRecord> srvRecords = new ArrayList<>();
      Properties environment = new Properties();
      environment.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
      DirContext context = new InitialDirContext(environment);
      Attributes dnsEntries = context.getAttributes(serviceName, new String[]{"SRV"});
      if (dnsEntries != null) {
         Attribute hosts = dnsEntries.get("SRV");
         if (hosts != null) {
            for (int i = 0; i < hosts.size(); i++) {
               srvRecords.add(DnsSrv.SrvRecord.buildFrom((String)hosts.get(i)));
            }
         }
      }

      return sortSrvRecords(srvRecords);
   }

   public static List<DnsSrv.SrvRecord> sortSrvRecords(List<DnsSrv.SrvRecord> srvRecords) {
      List<DnsSrv.SrvRecord> srvRecordsSortedNatural = srvRecords.stream().sorted().collect(Collectors.toList());
      Random random = new Random(System.nanoTime());
      List<DnsSrv.SrvRecord> srvRecordsSortedRfc2782 = new ArrayList<>();

      for (Integer priority : srvRecordsSortedNatural.stream().map(DnsSrv.SrvRecord::getPriority).distinct().collect(Collectors.toList())) {
         List<DnsSrv.SrvRecord> srvRecordsSamePriority = srvRecordsSortedNatural.stream().filter(s -> s.getPriority() == priority).collect(Collectors.toList());

         while (srvRecordsSamePriority.size() > 1) {
            int recCount = srvRecordsSamePriority.size();
            int sumOfWeights = 0;
            int[] weights = new int[recCount];

            for (int i = 0; i < recCount; i++) {
               sumOfWeights += srvRecordsSamePriority.get(i).getWeight();
               weights[i] = sumOfWeights;
            }

            int selection = random.nextInt(sumOfWeights + 1);
            int pos = 0;

            while (pos < recCount && weights[pos] < selection) {
               pos++;
            }

            srvRecordsSortedRfc2782.add(srvRecordsSamePriority.remove(pos));
         }

         srvRecordsSortedRfc2782.add(srvRecordsSamePriority.get(0));
      }

      return srvRecordsSortedRfc2782;
   }

   public static class SrvRecord implements Comparable<DnsSrv.SrvRecord> {
      private final int priority;
      private final int weight;
      private final int port;
      private final String target;

      public static DnsSrv.SrvRecord buildFrom(String srvLine) {
         String[] srvParts = srvLine.split("\\s+");
         if (srvParts.length == 4) {
            int priority = Integer.parseInt(srvParts[0]);
            int weight = Integer.parseInt(srvParts[1]);
            int port = Integer.parseInt(srvParts[2]);
            String target = srvParts[3].replaceFirst("\\.$", "");
            return new DnsSrv.SrvRecord(priority, weight, port, target);
         } else {
            return null;
         }
      }

      public SrvRecord(int priority, int weight, int port, String target) {
         this.priority = priority;
         this.weight = weight;
         this.port = port;
         this.target = target;
      }

      public int getPriority() {
         return this.priority;
      }

      public int getWeight() {
         return this.weight;
      }

      public int getPort() {
         return this.port;
      }

      public String getTarget() {
         return this.target;
      }

      @Override
      public String toString() {
         return String.format(
            "{\"Priority\": %d, \"Weight\": %d, \"Port\": %d, \"Target\": \"%s\"}", this.getPriority(), this.getWeight(), this.getPort(), this.getTarget()
         );
      }

      public int compareTo(DnsSrv.SrvRecord o) {
         int priorityDiff = this.getPriority() - o.getPriority();
         return priorityDiff == 0 ? this.getWeight() - o.getWeight() : priorityDiff;
      }
   }
}
