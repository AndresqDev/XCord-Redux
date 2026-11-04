package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.PropertyComponentBuilder;

class DefaultPropertyComponentBuilder extends DefaultComponentAndConfigurationBuilder<PropertyComponentBuilder> implements PropertyComponentBuilder {
   public DefaultPropertyComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, var2, "Property", var3);
   }
}
