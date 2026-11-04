package org.codehaus.plexus.interpolation.fixed;

import org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor;

public class ObjectBasedValueSource implements FixedValueSource {
   private final Object root;

   public ObjectBasedValueSource(Object var1) {
      this.root = var1;
   }

   @Override
   public Object getValue(String var1, InterpolationState var2) {
      if (var1 != null && var1.trim().length() >= 1) {
         try {
            return ReflectionValueExtractor.evaluate(var1, this.root, false);
         } catch (Exception var4) {
            var2.addFeedback("Failed to extract '" + var1 + "' from: " + this.root, var4);
            return null;
         }
      } else {
         return null;
      }
   }
}
