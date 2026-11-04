package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportSet extends ConfigurationContainer implements Serializable, Cloneable {
   private String id = "default";
   private List<String> reports;

   public void addReport(String var1) {
      this.getReports().add(var1);
   }

   public ReportSet clone() {
      try {
         ReportSet var1 = (ReportSet)super.clone();
         if (this.reports != null) {
            var1.reports = new ArrayList<>();
            var1.reports.addAll(this.reports);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getId() {
      return this.id;
   }

   public List<String> getReports() {
      if (this.reports == null) {
         this.reports = new ArrayList<>();
      }

      return this.reports;
   }

   public void removeReport(String var1) {
      this.getReports().remove(var1);
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setReports(List<String> var1) {
      this.reports = var1;
   }

   @Override
   public String toString() {
      return this.getId();
   }
}
