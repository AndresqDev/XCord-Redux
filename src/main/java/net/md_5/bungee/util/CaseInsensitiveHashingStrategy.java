package net.md_5.bungee.util;

import gnu.trove.strategy.HashingStrategy;
import java.util.Locale;

class CaseInsensitiveHashingStrategy implements HashingStrategy {
   static final CaseInsensitiveHashingStrategy INSTANCE = new CaseInsensitiveHashingStrategy();

   public int computeHashCode(Object var1) {
      return ((String)var1).toLowerCase(Locale.ROOT).hashCode();
   }

   public boolean equals(Object var1, Object var2) {
      return var1.equals(var2)
         || var1 instanceof String && var2 instanceof String && ((String)var1).toLowerCase(Locale.ROOT).equals(((String)var2).toLowerCase(Locale.ROOT));
   }
}
