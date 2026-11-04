package io.netty.util.internal.shaded.org.jctools.util;

public final class UnsafeRefArrayAccess {
   public static final long REF_ARRAY_BASE;
   public static final int REF_ELEMENT_SHIFT;

   public static <E> void spRefElement(E[] var0, long var1, E var3) {
      UnsafeAccess.UNSAFE.putObject(var0, var1, var3);
   }

   public static <E> void soRefElement(E[] var0, long var1, E var3) {
      UnsafeAccess.UNSAFE.putOrderedObject(var0, var1, var3);
   }

   public static <E> E lpRefElement(E[] var0, long var1) {
      return (E)UnsafeAccess.UNSAFE.getObject(var0, var1);
   }

   public static <E> E lvRefElement(E[] var0, long var1) {
      return (E)UnsafeAccess.UNSAFE.getObjectVolatile(var0, var1);
   }

   public static long calcRefElementOffset(long var0) {
      return REF_ARRAY_BASE + (var0 << REF_ELEMENT_SHIFT);
   }

   public static long calcCircularRefElementOffset(long var0, long var2) {
      return REF_ARRAY_BASE + ((var0 & var2) << REF_ELEMENT_SHIFT);
   }

   public static <E> E[] allocateRefArray(int var0) {
      return (E[])(new Object[var0]);
   }

   static {
      int var0 = UnsafeAccess.UNSAFE.arrayIndexScale(Object[].class);
      if (4 == var0) {
         REF_ELEMENT_SHIFT = 2;
      } else {
         if (8 != var0) {
            throw new IllegalStateException("Unknown pointer size: " + var0);
         }

         REF_ELEMENT_SHIFT = 3;
      }

      REF_ARRAY_BASE = (long)UnsafeAccess.UNSAFE.arrayBaseOffset(Object[].class);
   }
}
