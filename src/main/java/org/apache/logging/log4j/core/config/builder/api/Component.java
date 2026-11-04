package org.apache.logging.log4j.core.config.builder.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Component {
   private final Map<String, String> attributes = new LinkedHashMap<>();
   private final List<Component> components = new ArrayList<>();
   private final String pluginType;
   private final String value;

   public Component(String var1) {
      this(var1, null, null);
   }

   public Component(String var1, String var2) {
      this(var1, var2, null);
   }

   public Component(String var1, String var2, String var3) {
      this.pluginType = var1;
      this.value = var3;
      if (var2 != null && var2.length() > 0) {
         this.attributes.put("name", var2);
      }
   }

   public Component() {
      this.pluginType = null;
      this.value = null;
   }

   public String addAttribute(String var1, String var2) {
      return this.attributes.put(var1, var2);
   }

   public void addComponent(Component var1) {
      this.components.add(var1);
   }

   public Map<String, String> getAttributes() {
      return this.attributes;
   }

   public List<Component> getComponents() {
      return this.components;
   }

   public String getPluginType() {
      return this.pluginType;
   }

   public String getValue() {
      return this.value;
   }
}
