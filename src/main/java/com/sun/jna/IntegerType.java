package com.sun.jna;

public abstract class IntegerType extends Number implements NativeMapped {
   private static final long serialVersionUID = 1L;
   private int size;
   private Number number;
   private boolean unsigned;
   private long value;

   public IntegerType(int var1) {
      this(var1, 0L, false);
   }

   public IntegerType(int var1, boolean var2) {
      this(var1, 0L, var2);
   }

   public IntegerType(int var1, long var2) {
      this(var1, var2, false);
   }

   public IntegerType(int var1, long var2, boolean var4) {
      this.size = var1;
      this.unsigned = var4;
      this.setValue(var2);
   }

   public void setValue(long var1) {
      long var3 = var1;
      this.value = var1;
      switch (this.size) {
         case 1:
            if (this.unsigned) {
               this.value = var1 & 255L;
            }

            var3 = (long)((byte)((int)var1));
            this.number = (byte)((int)var1);
            break;
         case 2:
            if (this.unsigned) {
               this.value = var1 & 65535L;
            }

            var3 = (long)((short)((int)var1));
            this.number = (short)((int)var1);
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw new IllegalArgumentException("Unsupported size: " + this.size);
         case 4:
            if (this.unsigned) {
               this.value = var1 & 4294967295L;
            }

            var3 = (long)((int)var1);
            this.number = (int)var1;
            break;
         case 8:
            this.number = var1;
      }

      if (this.size < 8) {
         long var5 = ~((1L << this.size * 8) - 1L);
         if (var1 < 0L && var3 != var1 || var1 >= 0L && (var5 & var1) != 0L) {
            throw new IllegalArgumentException(
               "Argument value 0x" + Long.toHexString(var1) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(var5)
            );
         }
      }
   }

   @Override
   public Object toNative() {
      return this.number;
   }

   @Override
   public Object fromNative(Object var1, FromNativeContext var2) {
      long var3 = var1 == null ? 0L : ((Number)var1).longValue();

      try {
         IntegerType var5 = (IntegerType)this.getClass().newInstance();
         var5.setValue(var3);
         return var5;
      } catch (InstantiationException var6) {
         throw new IllegalArgumentException("Can't instantiate " + this.getClass());
      } catch (IllegalAccessException var7) {
         throw new IllegalArgumentException("Not allowed to instantiate " + this.getClass());
      }
   }

   @Override
   public Class<?> nativeType() {
      return this.number.getClass();
   }

   @Override
   public int intValue() {
      return (int)this.value;
   }

   @Override
   public long longValue() {
      return this.value;
   }

   @Override
   public float floatValue() {
      return this.number.floatValue();
   }

   @Override
   public double doubleValue() {
      return this.number.doubleValue();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof IntegerType && this.number.equals(((IntegerType)var1).number);
   }

   @Override
   public String toString() {
      return this.number.toString();
   }

   @Override
   public int hashCode() {
      return this.number.hashCode();
   }

   public static <T extends IntegerType> int compare(T var0, T var1) {
      if (var0 == var1) {
         return 0;
      } else if (var0 == null) {
         return 1;
      } else {
         return var1 == null ? -1 : compare(var0.longValue(), var1.longValue());
      }
   }

   public static int compare(IntegerType var0, long var1) {
      return var0 == null ? 1 : compare(var0.longValue(), var1);
   }

   public static final int compare(long var0, long var2) {
      if (var0 == var2) {
         return 0;
      } else {
         return var0 < var2 ? -1 : 1;
      }
   }
}
