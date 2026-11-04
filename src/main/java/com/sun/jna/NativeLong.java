package com.sun.jna;

public class NativeLong extends IntegerType {
   private static final long serialVersionUID = 1L;
   public static final int SIZE = Native.LONG_SIZE;

   public NativeLong() {
      this(0L);
   }

   public NativeLong(long var1) {
      this(var1, false);
   }

   public NativeLong(long var1, boolean var3) {
      super(SIZE, var1, var3);
   }
}
