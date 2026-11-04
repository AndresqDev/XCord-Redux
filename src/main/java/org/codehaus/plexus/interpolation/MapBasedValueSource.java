package org.codehaus.plexus.interpolation;

import java.util.Map;

public class MapBasedValueSource extends AbstractValueSource {
   private final Map values;

   public MapBasedValueSource(Map var1) {
      super(false);
      this.values = var1;
   }

   @Override
   public Object getValue(String var1) {
      return this.values == null ? null : this.values.get(var1);
   }
}
