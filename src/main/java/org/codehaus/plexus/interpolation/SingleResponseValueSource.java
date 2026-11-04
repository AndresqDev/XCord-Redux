package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;

public class SingleResponseValueSource implements ValueSource {
   private final String expression;
   private final Object response;

   public SingleResponseValueSource(String var1, Object var2) {
      this.expression = var1;
      this.response = var2;
   }

   @Override
   public void clearFeedback() {
   }

   @Override
   public List getFeedback() {
      return Collections.EMPTY_LIST;
   }

   @Override
   public Object getValue(String var1) {
      return this.expression.equals(var1) ? this.response : null;
   }
}
