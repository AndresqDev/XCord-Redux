package org.jline.reader.impl;

import java.util.Objects;
import org.jline.reader.MaskingCallback;

public final class SimpleMaskingCallback implements MaskingCallback {
   private final Character mask;

   public SimpleMaskingCallback(Character var1) {
      this.mask = Objects.requireNonNull(var1, "mask must be a non null character");
   }

   @Override
   public String display(String var1) {
      if (this.mask.equals('\u0000')) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(var1.length());
         int var3 = var1.length();

         while (var3-- > 0) {
            var2.append(this.mask.charValue());
         }

         return var2.toString();
      }
   }

   @Override
   public String history(String var1) {
      return null;
   }
}
