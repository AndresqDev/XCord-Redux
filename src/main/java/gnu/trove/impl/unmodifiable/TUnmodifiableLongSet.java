package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TLongSet;
import java.io.Serializable;

public class TUnmodifiableLongSet extends TUnmodifiableLongCollection implements TLongSet, Serializable {
   private static final long serialVersionUID = -9215047833775013803L;

   public TUnmodifiableLongSet(TLongSet var1) {
      super(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.c.equals(var1);
   }

   @Override
   public int hashCode() {
      return this.c.hashCode();
   }
}
