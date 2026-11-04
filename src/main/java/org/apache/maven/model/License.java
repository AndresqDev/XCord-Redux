package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class License implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String url;
   private String distribution;
   private String comments;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation nameLocation;
   private InputLocation urlLocation;
   private InputLocation distributionLocation;
   private InputLocation commentsLocation;

   public License clone() {
      try {
         License var1 = (License)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getComments() {
      return this.comments;
   }

   public String getDistribution() {
      return this.distribution;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "name":
               return this.nameLocation;
            case "url":
               return this.urlLocation;
            case "distribution":
               return this.distributionLocation;
            case "comments":
               return this.commentsLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getName() {
      return this.name;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "name":
               this.nameLocation = var2;
               return;
            case "url":
               this.urlLocation = var2;
               return;
            case "distribution":
               this.distributionLocation = var2;
               return;
            case "comments":
               this.commentsLocation = var2;
               return;
            default:
               this.setOtherLocation(var1, var2);
         }
      } else {
         this.setOtherLocation(var1, var2);
      }
   }

   public void setOtherLocation(Object var1, InputLocation var2) {
      if (var2 != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap<>();
         }

         this.locations.put(var1, var2);
      }
   }

   private InputLocation getOtherLocation(Object var1) {
      return this.locations != null ? this.locations.get(var1) : null;
   }

   public String getUrl() {
      return this.url;
   }

   public void setComments(String var1) {
      this.comments = var1;
   }

   public void setDistribution(String var1) {
      this.distribution = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }
}
