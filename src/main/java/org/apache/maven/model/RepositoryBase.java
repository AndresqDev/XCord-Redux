package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepositoryBase implements Serializable, Cloneable, InputLocationTracker {
   private String id;
   private String name;
   private String url;
   private String layout = "default";
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation idLocation;
   private InputLocation nameLocation;
   private InputLocation urlLocation;
   private InputLocation layoutLocation;

   public RepositoryBase clone() {
      try {
         RepositoryBase var1 = (RepositoryBase)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof RepositoryBase)) {
         return false;
      } else {
         RepositoryBase var2 = (RepositoryBase)var1;
         boolean var3 = true;
         return var3 && (this.getId() == null ? var2.getId() == null : this.getId().equals(var2.getId()));
      }
   }

   public String getId() {
      return this.id;
   }

   public String getLayout() {
      return this.layout;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "id":
               return this.idLocation;
            case "name":
               return this.nameLocation;
            case "url":
               return this.urlLocation;
            case "layout":
               return this.layoutLocation;
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
            case "id":
               this.idLocation = var2;
               return;
            case "name":
               this.nameLocation = var2;
               return;
            case "url":
               this.urlLocation = var2;
               return;
            case "layout":
               this.layoutLocation = var2;
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

   @Override
   public int hashCode() {
      byte var1 = 17;
      return 37 * var1 + (this.id != null ? this.id.hashCode() : 0);
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setLayout(String var1) {
      this.layout = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append("id = '");
      var1.append(this.getId());
      var1.append("'");
      return var1.toString();
   }
}
