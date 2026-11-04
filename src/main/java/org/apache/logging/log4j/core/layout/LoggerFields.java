package org.apache.logging.log4j.core.layout;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.StructuredDataId;

@Plugin(
   name = "LoggerFields",
   category = "Core",
   printObject = true
)
public final class LoggerFields {
   private final Map<String, String> map;
   private final String sdId;
   private final String enterpriseId;
   private final boolean discardIfAllFieldsAreEmpty;

   private LoggerFields(Map<String, String> var1, String var2, String var3, boolean var4) {
      this.sdId = var2;
      this.enterpriseId = var3;
      this.map = Collections.unmodifiableMap(var1);
      this.discardIfAllFieldsAreEmpty = var4;
   }

   public Map<String, String> getMap() {
      return this.map;
   }

   @Override
   public String toString() {
      return this.map.toString();
   }

   @PluginFactory
   public static LoggerFields createLoggerFields(
      @PluginElement("LoggerFields") KeyValuePair[] var0,
      @PluginAttribute("sdId") String var1,
      @PluginAttribute("enterpriseId") String var2,
      @PluginAttribute("discardIfAllFieldsAreEmpty") boolean var3
   ) {
      HashMap var4 = new HashMap();

      for (KeyValuePair var8 : var0) {
         var4.put(var8.getKey(), var8.getValue());
      }

      return new LoggerFields(var4, var1, var2, var3);
   }

   public StructuredDataId getSdId() {
      if (this.enterpriseId != null && this.sdId != null) {
         int var1 = Integer.parseInt(this.enterpriseId);
         return new StructuredDataId(this.sdId, var1, null, null);
      } else {
         return null;
      }
   }

   public boolean getDiscardIfAllFieldsAreEmpty() {
      return this.discardIfAllFieldsAreEmpty;
   }
}
