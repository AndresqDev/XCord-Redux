package net.md_5.bungee.util;

import gnu.trove.set.hash.TCustomHashSet;
import java.util.Collection;

public class CaseInsensitiveSet extends TCustomHashSet<String> {
   public CaseInsensitiveSet() {
      super(CaseInsensitiveHashingStrategy.INSTANCE);
   }

   public CaseInsensitiveSet(Collection<? extends String> var1) {
      super(CaseInsensitiveHashingStrategy.INSTANCE, var1);
   }
}
