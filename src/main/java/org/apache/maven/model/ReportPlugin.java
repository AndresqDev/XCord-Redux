package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportPlugin extends ConfigurationContainer implements Serializable, Cloneable {
   private String groupId = "org.apache.maven.plugins";
   private String artifactId;
   private String version;
   private List<ReportSet> reportSets;
   private Map<String, ReportSet> reportSetMap = null;

   public void addReportSet(ReportSet var1) {
      this.getReportSets().add(var1);
   }

   public ReportPlugin clone() {
      try {
         ReportPlugin var1 = (ReportPlugin)super.clone();
         if (this.reportSets != null) {
            var1.reportSets = new ArrayList<>();

            for (ReportSet var3 : this.reportSets) {
               var1.reportSets.add(var3.clone());
            }
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public List<ReportSet> getReportSets() {
      if (this.reportSets == null) {
         this.reportSets = new ArrayList<>();
      }

      return this.reportSets;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeReportSet(ReportSet var1) {
      this.getReportSets().remove(var1);
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public void setReportSets(List<ReportSet> var1) {
      this.reportSets = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public void flushReportSetMap() {
      this.reportSetMap = null;
   }

   public Map<String, ReportSet> getReportSetsAsMap() {
      if (this.reportSetMap == null) {
         this.reportSetMap = new LinkedHashMap<>();
         if (this.getReportSets() != null) {
            for (ReportSet var2 : this.getReportSets()) {
               this.reportSetMap.put(var2.getId(), var2);
            }
         }
      }

      return this.reportSetMap;
   }

   public String getKey() {
      return constructKey(this.groupId, this.artifactId);
   }

   public static String constructKey(String var0, String var1) {
      return var0 + ":" + var1;
   }
}
