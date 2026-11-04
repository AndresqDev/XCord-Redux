package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Site implements Serializable, Cloneable, InputLocationTracker {
   private String id;
   private String name;
   private String url;
   private String childSiteUrlInheritAppendPath;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation idLocation;
   private InputLocation nameLocation;
   private InputLocation urlLocation;
   private InputLocation childSiteUrlInheritAppendPathLocation;

   public Site clone() {
      try {
         Site var1 = (Site)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getChildSiteUrlInheritAppendPath() {
      return this.childSiteUrlInheritAppendPath;
   }

   public String getId() {
      return this.id;
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
            case "childSiteUrlInheritAppendPath":
               return this.childSiteUrlInheritAppendPathLocation;
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
            case "childSiteUrlInheritAppendPath":
               this.childSiteUrlInheritAppendPathLocation = var2;
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

   public void setChildSiteUrlInheritAppendPath(String var1) {
      this.childSiteUrlInheritAppendPath = var1;
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }

   public boolean isChildSiteUrlInheritAppendPath() {
      return this.childSiteUrlInheritAppendPath != null ? Boolean.parseBoolean(this.childSiteUrlInheritAppendPath) : true;
   }

   public void setChildSiteUrlInheritAppendPath(boolean var1) {
      this.childSiteUrlInheritAppendPath = String.valueOf(var1);
   }
}
