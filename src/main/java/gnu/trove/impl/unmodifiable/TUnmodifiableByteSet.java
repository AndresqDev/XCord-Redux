package gnu.trove.impl.unmodifiable;

import gnu.trove.set.TByteSet;
import java.io.Serializable;

public class TUnmodifiableByteSet extends TUnmodifiableByteCollection implements TByteSet, Serializable {
   private static final long serialVersionUID = -9215047833775013803L;

   public TUnmodifiableByteSet(TByteSet var1) {
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
