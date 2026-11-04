package org.codehaus.plexus.interpolation;

import java.util.Properties;

public class PropertiesBasedValueSource extends AbstractValueSource {
   private final Properties properties;

   public PropertiesBasedValueSource(Properties var1) {
      super(false);
      this.properties = var1;
   }

   @Override
   public Object getValue(String var1) {
      return this.properties == null ? null : this.properties.getProperty(var1);
   }
}
