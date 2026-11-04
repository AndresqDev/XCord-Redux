package gnu.trove.procedure.array;

import gnu.trove.procedure.TObjectProcedure;

public final class ToObjectArrayProceedure<T> implements TObjectProcedure<T> {
   private final T[] target;
   private int pos = 0;

   public ToObjectArrayProceedure(T[] var1) {
      this.target = (T[])var1;
   }

   @Override
   public final boolean execute(T var1) {
      this.target[this.pos++] = (T)var1;
      return true;
   }
}
