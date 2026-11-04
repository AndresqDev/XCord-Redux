package org.codehaus.plexus.interpolation.fixed;

import java.util.Properties;

public class PropertiesBasedValueSource implements FixedValueSource {
   private final Properties properties;

   public PropertiesBasedValueSource(Properties var1) {
      this.properties = var1;
   }

   @Override
   public Object getValue(String var1, InterpolationState var2) {
      return this.properties == null ? null : this.properties.getProperty(var1);
   }
}
