package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Contributor implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String email;
   private String url;
   private String organization;
   private String organizationUrl;
   private List<String> roles;
   private String timezone;
   private Properties properties;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation nameLocation;
   private InputLocation emailLocation;
   private InputLocation urlLocation;
   private InputLocation organizationLocation;
   private InputLocation organizationUrlLocation;
   private InputLocation rolesLocation;
   private InputLocation timezoneLocation;
   private InputLocation propertiesLocation;

   public void addProperty(String var1, String var2) {
      this.getProperties().put(var1, var2);
   }

   public void addRole(String var1) {
      this.getRoles().add(var1);
   }

   public Contributor clone() {
      try {
         Contributor var1 = (Contributor)super.clone();
         if (this.roles != null) {
            var1.roles = new ArrayList<>();
            var1.roles.addAll(this.roles);
         }

         if (this.properties != null) {
            var1.properties = (Properties)this.properties.clone();
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getEmail() {
      return this.email;
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
            case "email":
               return this.emailLocation;
            case "url":
               return this.urlLocation;
            case "organization":
               return this.organizationLocation;
            case "organizationUrl":
               return this.organizationUrlLocation;
            case "roles":
               return this.rolesLocation;
            case "timezone":
               return this.timezoneLocation;
            case "properties":
               return this.propertiesLocation;
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

   public String getOrganization() {
      return this.organization;
   }

   public String getOrganizationUrl() {
      return this.organizationUrl;
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
            case "email":
               this.emailLocation = var2;
               return;
            case "url":
               this.urlLocation = var2;
               return;
            case "organization":
               this.organizationLocation = var2;
               return;
            case "organizationUrl":
               this.organizationUrlLocation = var2;
               return;
            case "roles":
               this.rolesLocation = var2;
               return;
            case "timezone":
               this.timezoneLocation = var2;
               return;
            case "properties":
               this.propertiesLocation = var2;
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

   public Properties getProperties() {
      if (this.properties == null) {
         this.properties = new Properties();
      }

      return this.properties;
   }

   public List<String> getRoles() {
      if (this.roles == null) {
         this.roles = new ArrayList<>();
      }

      return this.roles;
   }

   public String getTimezone() {
      return this.timezone;
   }

   public String getUrl() {
      return this.url;
   }

   public void removeRole(String var1) {
      this.getRoles().remove(var1);
   }

   public void setEmail(String var1) {
      this.email = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setOrganization(String var1) {
      this.organization = var1;
   }

   public void setOrganizationUrl(String var1) {
      this.organizationUrl = var1;
   }

   public void setProperties(Properties var1) {
      this.properties = var1;
   }

   public void setRoles(List<String> var1) {
      this.roles = var1;
   }

   public void setTimezone(String var1) {
      this.timezone = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }
}
