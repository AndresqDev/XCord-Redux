package org.codehaus.plexus.interpolation;

import java.util.List;

public class PrefixedObjectValueSource extends AbstractDelegatingValueSource implements QueryEnabledValueSource {
   public PrefixedObjectValueSource(String var1, Object var2) {
      super(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(var2), var1));
   }

   public PrefixedObjectValueSource(List<String> var1, Object var2, boolean var3) {
      super(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(var2), var1, var3));
   }

   @Override
   public String getLastExpression() {
      return ((QueryEnabledValueSource)this.getDelegate()).getLastExpression();
   }
}
