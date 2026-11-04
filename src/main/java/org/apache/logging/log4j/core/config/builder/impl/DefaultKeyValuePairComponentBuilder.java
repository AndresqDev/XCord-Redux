package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.KeyValuePairComponentBuilder;

class DefaultKeyValuePairComponentBuilder extends DefaultComponentAndConfigurationBuilder<KeyValuePairComponentBuilder> implements KeyValuePairComponentBuilder {
   public DefaultKeyValuePairComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, "KeyValuePair");
      this.addAttribute("key", var2);
      this.addAttribute("value", var3);
   }
}
