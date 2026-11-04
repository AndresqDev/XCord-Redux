package org.apache.maven.model.profile;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefaultProfileActivationContext implements ProfileActivationContext {
   private List<String> activeProfileIds = Collections.emptyList();
   private List<String> inactiveProfileIds = Collections.emptyList();
   private Map<String, String> systemProperties = Collections.emptyMap();
   private Map<String, String> userProperties = Collections.emptyMap();
   private Map<String, String> projectProperties = Collections.emptyMap();
   private File projectDirectory;

   @Override
   public List<String> getActiveProfileIds() {
      return this.activeProfileIds;
   }

   public DefaultProfileActivationContext setActiveProfileIds(List<String> var1) {
      if (var1 != null) {
         this.activeProfileIds = Collections.unmodifiableList(var1);
      } else {
         this.activeProfileIds = Collections.emptyList();
      }

      return this;
   }

   @Override
   public List<String> getInactiveProfileIds() {
      return this.inactiveProfileIds;
   }

   public DefaultProfileActivationContext setInactiveProfileIds(List<String> var1) {
      if (var1 != null) {
         this.inactiveProfileIds = Collections.unmodifiableList(var1);
      } else {
         this.inactiveProfileIds = Collections.emptyList();
      }

      return this;
   }

   @Override
   public Map<String, String> getSystemProperties() {
      return this.systemProperties;
   }

   public DefaultProfileActivationContext setSystemProperties(Properties var1) {
      if (var1 != null) {
         this.systemProperties = Collections.unmodifiableMap(var1);
      } else {
         this.systemProperties = Collections.emptyMap();
      }

      return this;
   }

   public DefaultProfileActivationContext setSystemProperties(Map<String, String> var1) {
      if (var1 != null) {
         this.systemProperties = Collections.unmodifiableMap(var1);
      } else {
         this.systemProperties = Collections.emptyMap();
      }

      return this;
   }

   @Override
   public Map<String, String> getUserProperties() {
      return this.userProperties;
   }

   public DefaultProfileActivationContext setUserProperties(Properties var1) {
      if (var1 != null) {
         this.userProperties = Collections.unmodifiableMap(var1);
      } else {
         this.userProperties = Collections.emptyMap();
      }

      return this;
   }

   public DefaultProfileActivationContext setUserProperties(Map<String, String> var1) {
      if (var1 != null) {
         this.userProperties = Collections.unmodifiableMap(var1);
      } else {
         this.userProperties = Collections.emptyMap();
      }

      return this;
   }

   @Override
   public File getProjectDirectory() {
      return this.projectDirectory;
   }

   public DefaultProfileActivationContext setProjectDirectory(File var1) {
      this.projectDirectory = var1;
      return this;
   }

   @Override
   public Map<String, String> getProjectProperties() {
      return this.projectProperties;
   }

   public DefaultProfileActivationContext setProjectProperties(Properties var1) {
      if (var1 != null) {
         this.projectProperties = Collections.unmodifiableMap(this.toMap(var1));
      } else {
         this.projectProperties = Collections.emptyMap();
      }

      return this;
   }

   private Map<String, String> toMap(Properties var1) {
      if (var1 == null) {
         return Collections.emptyMap();
      } else {
         HashMap var2 = new HashMap();
         Enumeration var3 = var1.keys();

         while (var3.hasMoreElements()) {
            String var4 = (String)var3.nextElement();
            var2.put(var4, var1.getProperty(var4));
         }

         return var2;
      }
   }
}
