package org.codehaus.plexus.interpolation;

import java.util.List;
import java.util.Properties;

public class PrefixedPropertiesValueSource extends AbstractDelegatingValueSource implements QueryEnabledValueSource {
   public PrefixedPropertiesValueSource(String var1, Properties var2) {
      super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(var2), var1));
   }

   public PrefixedPropertiesValueSource(List<String> var1, Properties var2, boolean var3) {
      super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(var2), var1, var3));
   }

   @Override
   public String getLastExpression() {
      return ((QueryEnabledValueSource)this.getDelegate()).getLastExpression();
   }
}
