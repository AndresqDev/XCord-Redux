package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.ArrayUtils;

@Deprecated
public class AggregateTranslator extends CharSequenceTranslator {
   private final CharSequenceTranslator[] translators;

   public AggregateTranslator(CharSequenceTranslator... var1) {
      this.translators = ArrayUtils.clone(var1);
   }

   @Override
   public int translate(CharSequence var1, int var2, Writer var3) throws IOException {
      for (CharSequenceTranslator var7 : this.translators) {
         int var8 = var7.translate(var1, var2, var3);
         if (var8 != 0) {
            return var8;
         }
      }

      return 0;
   }
}
