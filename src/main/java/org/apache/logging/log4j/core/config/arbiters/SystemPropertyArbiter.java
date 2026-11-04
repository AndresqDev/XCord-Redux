package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "SystemPropertyArbiter",
   category = "Core",
   elementType = "Arbiter",
   deferChildren = true,
   printObject = true
)
public class SystemPropertyArbiter implements Arbiter {
   private final String propertyName;
   private final String propertyValue;

   private SystemPropertyArbiter(String var1, String var2) {
      this.propertyName = var1;
      this.propertyValue = var2;
   }

   @Override
   public boolean isCondition() {
      String var1 = System.getProperty(this.propertyName);
      return var1 != null && (this.propertyValue == null || var1.equals(this.propertyValue));
   }

   @PluginBuilderFactory
   public static SystemPropertyArbiter.Builder newBuilder() {
      return new SystemPropertyArbiter.Builder();
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<SystemPropertyArbiter> {
      public static final String ATTR_PROPERTY_NAME = "propertyName";
      public static final String ATTR_PROPERTY_VALUE = "propertyValue";
      @PluginBuilderAttribute("propertyName")
      private String propertyName;
      @PluginBuilderAttribute("propertyValue")
      private String propertyValue;

      public SystemPropertyArbiter.Builder setPropertyName(String var1) {
         this.propertyName = var1;
         return this.asBuilder();
      }

      public SystemPropertyArbiter.Builder setPropertyValue(String var1) {
         this.propertyName = var1;
         return this.asBuilder();
      }

      public SystemPropertyArbiter.Builder asBuilder() {
         return this;
      }

      public SystemPropertyArbiter build() {
         return new SystemPropertyArbiter(this.propertyName, this.propertyValue);
      }
   }
}
