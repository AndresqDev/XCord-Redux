package org.codehaus.plexus.interpolation.fixed;

import java.util.List;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

public class PrefixedValueSourceWrapper implements FixedValueSource {
   private final FixedValueSource valueSource;
   private final String[] possiblePrefixes;
   private boolean allowUnprefixedExpressions;
   private String lastExpression;

   public PrefixedValueSourceWrapper(FixedValueSource var1, String var2) {
      this.valueSource = var1;
      this.possiblePrefixes = new String[]{var2};
   }

   public PrefixedValueSourceWrapper(FixedValueSource var1, String var2, boolean var3) {
      this.valueSource = var1;
      this.possiblePrefixes = new String[]{var2};
      this.allowUnprefixedExpressions = var3;
   }

   public PrefixedValueSourceWrapper(FixedValueSource var1, List<String> var2) {
      this.valueSource = var1;
      this.possiblePrefixes = var2.toArray(new String[var2.size()]);
   }

   public PrefixedValueSourceWrapper(FixedValueSource var1, List<String> var2, boolean var3) {
      this.valueSource = var1;
      this.possiblePrefixes = var2.toArray(new String[var2.size()]);
      this.allowUnprefixedExpressions = var3;
   }

   @Override
   public Object getValue(String var1, InterpolationState var2) {
      var1 = ValueSourceUtils.trimPrefix(var1, this.possiblePrefixes, this.allowUnprefixedExpressions);
      return var1 == null ? null : this.valueSource.getValue(var1, var2);
   }
}
