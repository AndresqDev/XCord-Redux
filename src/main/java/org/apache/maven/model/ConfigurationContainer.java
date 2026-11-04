package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ConfigurationContainer implements Serializable, Cloneable, InputLocationTracker {
   private String inherited;
   private Object configuration;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation inheritedLocation;
   private InputLocation configurationLocation;
   private boolean inheritanceApplied = true;

   public ConfigurationContainer clone() {
      try {
         ConfigurationContainer var1 = (ConfigurationContainer)super.clone();
         if (this.configuration != null) {
            var1.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public Object getConfiguration() {
      return this.configuration;
   }

   public String getInherited() {
      return this.inherited;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "inherited":
               return this.inheritedLocation;
            case "configuration":
               return this.configurationLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "inherited":
               this.inheritedLocation = var2;
               return;
            case "configuration":
               this.configurationLocation = var2;
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

   public void setConfiguration(Object var1) {
      this.configuration = var1;
   }

   public void setInherited(String var1) {
      this.inherited = var1;
   }

   public boolean isInherited() {
      return this.inherited != null ? Boolean.parseBoolean(this.inherited) : true;
   }

   public void setInherited(boolean var1) {
      this.inherited = String.valueOf(var1);
   }

   public void unsetInheritanceApplied() {
      this.inheritanceApplied = false;
   }

   public boolean isInheritanceApplied() {
      return this.inheritanceApplied;
   }
}
