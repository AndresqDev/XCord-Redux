package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Notifier implements Serializable, Cloneable, InputLocationTracker {
   private String type = "mail";
   private boolean sendOnError = true;
   private boolean sendOnFailure = true;
   private boolean sendOnSuccess = true;
   private boolean sendOnWarning = true;
   private String address;
   private Properties configuration;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation typeLocation;
   private InputLocation sendOnErrorLocation;
   private InputLocation sendOnFailureLocation;
   private InputLocation sendOnSuccessLocation;
   private InputLocation sendOnWarningLocation;
   private InputLocation addressLocation;
   private InputLocation configurationLocation;

   public void addConfiguration(String var1, String var2) {
      this.getConfiguration().put(var1, var2);
   }

   public Notifier clone() {
      try {
         Notifier var1 = (Notifier)super.clone();
         if (this.configuration != null) {
            var1.configuration = (Properties)this.configuration.clone();
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getAddress() {
      return this.address;
   }

   public Properties getConfiguration() {
      if (this.configuration == null) {
         this.configuration = new Properties();
      }

      return this.configuration;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "type":
               return this.typeLocation;
            case "sendOnError":
               return this.sendOnErrorLocation;
            case "sendOnFailure":
               return this.sendOnFailureLocation;
            case "sendOnSuccess":
               return this.sendOnSuccessLocation;
            case "sendOnWarning":
               return this.sendOnWarningLocation;
            case "address":
               return this.addressLocation;
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
            case "type":
               this.typeLocation = var2;
               return;
            case "sendOnError":
               this.sendOnErrorLocation = var2;
               return;
            case "sendOnFailure":
               this.sendOnFailureLocation = var2;
               return;
            case "sendOnSuccess":
               this.sendOnSuccessLocation = var2;
               return;
            case "sendOnWarning":
               this.sendOnWarningLocation = var2;
               return;
            case "address":
               this.addressLocation = var2;
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

   public String getType() {
      return this.type;
   }

   public boolean isSendOnError() {
      return this.sendOnError;
   }

   public boolean isSendOnFailure() {
      return this.sendOnFailure;
   }

   public boolean isSendOnSuccess() {
      return this.sendOnSuccess;
   }

   public boolean isSendOnWarning() {
      return this.sendOnWarning;
   }

   public void setAddress(String var1) {
      this.address = var1;
   }

   public void setConfiguration(Properties var1) {
      this.configuration = var1;
   }

   public void setSendOnError(boolean var1) {
      this.sendOnError = var1;
   }

   public void setSendOnFailure(boolean var1) {
      this.sendOnFailure = var1;
   }

   public void setSendOnSuccess(boolean var1) {
      this.sendOnSuccess = var1;
   }

   public void setSendOnWarning(boolean var1) {
      this.sendOnWarning = var1;
   }

   public void setType(String var1) {
      this.type = var1;
   }
}
