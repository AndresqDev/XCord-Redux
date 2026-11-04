package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scm implements Serializable, Cloneable, InputLocationTracker {
   private String connection;
   private String developerConnection;
   private String tag = "HEAD";
   private String url;
   private String childScmConnectionInheritAppendPath;
   private String childScmDeveloperConnectionInheritAppendPath;
   private String childScmUrlInheritAppendPath;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation connectionLocation;
   private InputLocation developerConnectionLocation;
   private InputLocation tagLocation;
   private InputLocation urlLocation;
   private InputLocation childScmConnectionInheritAppendPathLocation;
   private InputLocation childScmDeveloperConnectionInheritAppendPathLocation;
   private InputLocation childScmUrlInheritAppendPathLocation;

   public Scm clone() {
      try {
         Scm var1 = (Scm)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getChildScmConnectionInheritAppendPath() {
      return this.childScmConnectionInheritAppendPath;
   }

   public String getChildScmDeveloperConnectionInheritAppendPath() {
      return this.childScmDeveloperConnectionInheritAppendPath;
   }

   public String getChildScmUrlInheritAppendPath() {
      return this.childScmUrlInheritAppendPath;
   }

   public String getConnection() {
      return this.connection;
   }

   public String getDeveloperConnection() {
      return this.developerConnection;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "connection":
               return this.connectionLocation;
            case "developerConnection":
               return this.developerConnectionLocation;
            case "tag":
               return this.tagLocation;
            case "url":
               return this.urlLocation;
            case "childScmConnectionInheritAppendPath":
               return this.childScmConnectionInheritAppendPathLocation;
            case "childScmDeveloperConnectionInheritAppendPath":
               return this.childScmDeveloperConnectionInheritAppendPathLocation;
            case "childScmUrlInheritAppendPath":
               return this.childScmUrlInheritAppendPathLocation;
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
            case "connection":
               this.connectionLocation = var2;
               return;
            case "developerConnection":
               this.developerConnectionLocation = var2;
               return;
            case "tag":
               this.tagLocation = var2;
               return;
            case "url":
               this.urlLocation = var2;
               return;
            case "childScmConnectionInheritAppendPath":
               this.childScmConnectionInheritAppendPathLocation = var2;
               return;
            case "childScmDeveloperConnectionInheritAppendPath":
               this.childScmDeveloperConnectionInheritAppendPathLocation = var2;
               return;
            case "childScmUrlInheritAppendPath":
               this.childScmUrlInheritAppendPathLocation = var2;
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

   public String getTag() {
      return this.tag;
   }

   public String getUrl() {
      return this.url;
   }

   public void setChildScmConnectionInheritAppendPath(String var1) {
      this.childScmConnectionInheritAppendPath = var1;
   }

   public void setChildScmDeveloperConnectionInheritAppendPath(String var1) {
      this.childScmDeveloperConnectionInheritAppendPath = var1;
   }

   public void setChildScmUrlInheritAppendPath(String var1) {
      this.childScmUrlInheritAppendPath = var1;
   }

   public void setConnection(String var1) {
      this.connection = var1;
   }

   public void setDeveloperConnection(String var1) {
      this.developerConnection = var1;
   }

   public void setTag(String var1) {
      this.tag = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }

   public boolean isChildScmConnectionInheritAppendPath() {
      return this.childScmConnectionInheritAppendPath != null ? Boolean.parseBoolean(this.childScmConnectionInheritAppendPath) : true;
   }

   public void setChildScmConnectionInheritAppendPath(boolean var1) {
      this.childScmConnectionInheritAppendPath = String.valueOf(var1);
   }

   public boolean isChildScmDeveloperConnectionInheritAppendPath() {
      return this.childScmDeveloperConnectionInheritAppendPath != null ? Boolean.parseBoolean(this.childScmDeveloperConnectionInheritAppendPath) : true;
   }

   public void setChildScmDeveloperConnectionInheritAppendPath(boolean var1) {
      this.childScmDeveloperConnectionInheritAppendPath = String.valueOf(var1);
   }

   public boolean isChildScmUrlInheritAppendPath() {
      return this.childScmUrlInheritAppendPath != null ? Boolean.parseBoolean(this.childScmUrlInheritAppendPath) : true;
   }

   public void setChildScmUrlInheritAppendPath(boolean var1) {
      this.childScmUrlInheritAppendPath = String.valueOf(var1);
   }
}
