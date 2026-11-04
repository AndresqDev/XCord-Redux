package org.apache.logging.log4j.core.config.builder.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;

class DefaultComponentBuilder<T extends ComponentBuilder<T>, CB extends ConfigurationBuilder<? extends Configuration>> implements ComponentBuilder<T> {
   private final CB builder;
   private final String type;
   private final Map<String, String> attributes = new LinkedHashMap<>();
   private final List<Component> components = new ArrayList<>();
   private final String name;
   private final String value;

   public DefaultComponentBuilder(CB var1, String var2) {
      this((CB)var1, null, var2, null);
   }

   public DefaultComponentBuilder(CB var1, String var2, String var3) {
      this((CB)var1, var2, var3, null);
   }

   public DefaultComponentBuilder(CB var1, String var2, String var3, String var4) {
      this.type = var3;
      this.builder = (CB)var1;
      this.name = var2;
      this.value = var4;
   }

   @Override
   public T addAttribute(String var1, boolean var2) {
      return this.put(var1, Boolean.toString(var2));
   }

   @Override
   public T addAttribute(String var1, Enum<?> var2) {
      return this.put(var1, var2.name());
   }

   @Override
   public T addAttribute(String var1, int var2) {
      return this.put(var1, Integer.toString(var2));
   }

   @Override
   public T addAttribute(String var1, Level var2) {
      return this.put(var1, var2.toString());
   }

   @Override
   public T addAttribute(String var1, Object var2) {
      return this.put(var1, var2.toString());
   }

   @Override
   public T addAttribute(String var1, String var2) {
      return this.put(var1, var2);
   }

   @Override
   public T addComponent(ComponentBuilder<?> var1) {
      this.components.add((Component)var1.build());
      return (T)this;
   }

   public Component build() {
      Component var1 = new Component(this.type, this.name, this.value);
      var1.getAttributes().putAll(this.attributes);
      var1.getComponents().addAll(this.components);
      return var1;
   }

   @Override
   public CB getBuilder() {
      return this.builder;
   }

   @Override
   public String getName() {
      return this.name;
   }

   protected T put(String var1, String var2) {
      this.attributes.put(var1, var2);
      return (T)this;
   }
}
