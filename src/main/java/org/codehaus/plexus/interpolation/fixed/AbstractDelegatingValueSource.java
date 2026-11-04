package org.codehaus.plexus.interpolation.fixed;

public abstract class AbstractDelegatingValueSource implements FixedValueSource {
   private final FixedValueSource delegate;

   protected AbstractDelegatingValueSource(FixedValueSource var1) {
      if (var1 == null) {
         throw new NullPointerException("Delegate ValueSource cannot be null.");
      } else {
         this.delegate = var1;
      }
   }

   protected FixedValueSource getDelegate() {
      return this.delegate;
   }

   @Override
   public Object getValue(String var1, InterpolationState var2) {
      return this.getDelegate().getValue(var1, var2);
   }
}
