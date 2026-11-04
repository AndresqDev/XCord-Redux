package org.apache.commons.lang3.tuple;

public final class ImmutablePair<L, R> extends Pair<L, R> {
   private static final ImmutablePair NULL = of(null, null);
   private static final long serialVersionUID = 4954918890077093841L;
   public final L left;
   public final R right;

   public static <L, R> ImmutablePair<L, R> nullPair() {
      return NULL;
   }

   public static <L, R> ImmutablePair<L, R> of(L var0, R var1) {
      return new ImmutablePair<>((L)var0, (R)var1);
   }

   public ImmutablePair(L var1, R var2) {
      this.left = (L)var1;
      this.right = (R)var2;
   }

   @Override
   public L getLeft() {
      return this.left;
   }

   @Override
   public R getRight() {
      return this.right;
   }

   @Override
   public R setValue(R var1) {
      throw new UnsupportedOperationException();
   }
}
