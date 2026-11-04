package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

public class PrefixedValueSourceWrapper implements FeedbackEnabledValueSource, QueryEnabledValueSource {
   private final ValueSource valueSource;
   private final String[] possiblePrefixes;
   private boolean allowUnprefixedExpressions;
   private String lastExpression;

   public PrefixedValueSourceWrapper(ValueSource var1, String var2) {
      this.valueSource = var1;
      this.possiblePrefixes = new String[]{var2};
   }

   public PrefixedValueSourceWrapper(ValueSource var1, String var2, boolean var3) {
      this.valueSource = var1;
      this.possiblePrefixes = new String[]{var2};
      this.allowUnprefixedExpressions = var3;
   }

   public PrefixedValueSourceWrapper(ValueSource var1, List<String> var2) {
      this.valueSource = var1;
      this.possiblePrefixes = var2.toArray(new String[var2.size()]);
   }

   public PrefixedValueSourceWrapper(ValueSource var1, List<String> var2, boolean var3) {
      this.valueSource = var1;
      this.possiblePrefixes = var2.toArray(new String[var2.size()]);
      this.allowUnprefixedExpressions = var3;
   }

   @Override
   public Object getValue(String var1) {
      this.lastExpression = ValueSourceUtils.trimPrefix(var1, this.possiblePrefixes, this.allowUnprefixedExpressions);
      return this.lastExpression == null ? null : this.valueSource.getValue(this.lastExpression);
   }

   @Override
   public List getFeedback() {
      return this.valueSource instanceof FeedbackEnabledValueSource ? this.valueSource.getFeedback() : Collections.EMPTY_LIST;
   }

   @Override
   public String getLastExpression() {
      return this.valueSource instanceof QueryEnabledValueSource ? ((QueryEnabledValueSource)this.valueSource).getLastExpression() : this.lastExpression;
   }

   @Override
   public void clearFeedback() {
      this.valueSource.clearFeedback();
   }
}
