package org.codehaus.plexus.interpolation;

import org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor;

public class ObjectBasedValueSource extends AbstractValueSource {
   private final Object root;

   public ObjectBasedValueSource(Object var1) {
      super(true);
      this.root = var1;
   }

   @Override
   public Object getValue(String var1) {
      if (var1 != null && var1.trim().length() >= 1) {
         try {
            return ReflectionValueExtractor.evaluate(var1, this.root, false);
         } catch (Exception var3) {
            this.addFeedback("Failed to extract '" + var1 + "' from: " + this.root, var3);
            return null;
         }
      } else {
         return null;
      }
   }
}
