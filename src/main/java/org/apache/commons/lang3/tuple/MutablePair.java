package org.apache.commons.lang3.tuple;

public class MutablePair<L, R> extends Pair<L, R> {
   private static final long serialVersionUID = 4954918890077093841L;
   public L left;
   public R right;

   public static <L, R> MutablePair<L, R> of(L var0, R var1) {
      return new MutablePair<>((L)var0, (R)var1);
   }

   public MutablePair() {
   }

   public MutablePair(L var1, R var2) {
      this.left = (L)var1;
      this.right = (R)var2;
   }

   @Override
   public L getLeft() {
      return this.left;
   }

   public void setLeft(L var1) {
      this.left = (L)var1;
   }

   @Override
   public R getRight() {
      return this.right;
   }

   public void setRight(R var1) {
      this.right = (R)var1;
   }

   @Override
   public R setValue(R var1) {
      Object var2 = this.getRight();
      this.setRight((R)var1);
      return (R)var2;
   }
}
