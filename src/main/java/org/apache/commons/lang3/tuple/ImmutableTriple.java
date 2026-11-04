package org.apache.commons.lang3.tuple;

public final class ImmutableTriple<L, M, R> extends Triple<L, M, R> {
   private static final ImmutableTriple NULL = of(null, null, null);
   private static final long serialVersionUID = 1L;
   public final L left;
   public final M middle;
   public final R right;

   public static <L, M, R> ImmutableTriple<L, M, R> nullTriple() {
      return NULL;
   }

   public static <L, M, R> ImmutableTriple<L, M, R> of(L var0, M var1, R var2) {
      return new ImmutableTriple<>((L)var0, (M)var1, (R)var2);
   }

   public ImmutableTriple(L var1, M var2, R var3) {
      this.left = (L)var1;
      this.middle = (M)var2;
      this.right = (R)var3;
   }

   @Override
   public L getLeft() {
      return this.left;
   }

   @Override
   public M getMiddle() {
      return this.middle;
   }

   @Override
   public R getRight() {
      return this.right;
   }
}
