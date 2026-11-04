package org.apache.commons.codec;

import java.util.Comparator;

public class StringEncoderComparator implements Comparator {
   private final StringEncoder stringEncoder;

   @Deprecated
   public StringEncoderComparator() {
      this.stringEncoder = null;
   }

   public StringEncoderComparator(StringEncoder var1) {
      this.stringEncoder = var1;
   }

   @Override
   public int compare(Object var1, Object var2) {
      int var3 = 0;

      try {
         Comparable var4 = (Comparable)this.stringEncoder.encode(var1);
         Comparable var5 = (Comparable)this.stringEncoder.encode(var2);
         var3 = var4.compareTo(var5);
      } catch (EncoderException var6) {
         var3 = 0;
      }

      return var3;
   }
}
