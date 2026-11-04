package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CiManagement implements Serializable, Cloneable, InputLocationTracker {
   private String system;
   private String url;
   private List<Notifier> notifiers;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation systemLocation;
   private InputLocation urlLocation;
   private InputLocation notifiersLocation;

   public void addNotifier(Notifier var1) {
      this.getNotifiers().add(var1);
   }

   public CiManagement clone() {
      try {
         CiManagement var1 = (CiManagement)super.clone();
         if (this.notifiers != null) {
            var1.notifiers = new ArrayList<>();

            for (Notifier var3 : this.notifiers) {
               var1.notifiers.add(var3.clone());
            }
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "system":
               return this.systemLocation;
            case "url":
               return this.urlLocation;
            case "notifiers":
               return this.notifiersLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public List<Notifier> getNotifiers() {
      if (this.notifiers == null) {
         this.notifiers = new ArrayList<>();
      }

      return this.notifiers;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "system":
               this.systemLocation = var2;
               return;
            case "url":
               this.urlLocation = var2;
               return;
            case "notifiers":
               this.notifiersLocation = var2;
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

   public String getSystem() {
      return this.system;
   }

   public String getUrl() {
      return this.url;
   }

   public void removeNotifier(Notifier var1) {
      this.getNotifiers().remove(var1);
   }

   public void setNotifiers(List<Notifier> var1) {
      this.notifiers = var1;
   }

   public void setSystem(String var1) {
      this.system = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }
}
