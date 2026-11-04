package gnu.trove.impl;

public final class HashFunctions {
   public static int hash(double var0) {
      assert !Double.isNaN(var0) : "Values of NaN are not supported.";

      long var2 = Double.doubleToLongBits(var0);
      return (int)(var2 ^ var2 >>> 32);
   }

   public static int hash(float var0) {
      assert !Float.isNaN(var0) : "Values of NaN are not supported.";

      return Float.floatToIntBits(var0 * 6.6360896E8F);
   }

   public static int hash(int var0) {
      return var0;
   }

   public static int hash(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   public static int hash(Object var0) {
      return var0 == null ? 0 : var0.hashCode();
   }
}
