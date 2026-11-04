package org.codehaus.plexus.interpolation.fixed;

import java.util.Map;

public class MapBasedValueSource implements FixedValueSource {
   private final Map values;

   public MapBasedValueSource(Map var1) {
      this.values = var1;
   }

   @Override
   public Object getValue(String var1, InterpolationState var2) {
      return this.values == null ? null : this.values.get(var1);
   }
}
