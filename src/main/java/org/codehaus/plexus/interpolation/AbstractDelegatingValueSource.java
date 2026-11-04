package org.codehaus.plexus.interpolation;

import java.util.List;

public abstract class AbstractDelegatingValueSource implements ValueSource {
   private final ValueSource delegate;

   protected AbstractDelegatingValueSource(ValueSource var1) {
      if (var1 == null) {
         throw new NullPointerException("Delegate ValueSource cannot be null.");
      } else {
         this.delegate = var1;
      }
   }

   protected ValueSource getDelegate() {
      return this.delegate;
   }

   @Override
   public Object getValue(String var1) {
      return this.getDelegate().getValue(var1);
   }

   @Override
   public void clearFeedback() {
      this.delegate.clearFeedback();
   }

   @Override
   public List getFeedback() {
      return this.delegate.getFeedback();
   }
}
