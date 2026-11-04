package org.fusesource.hawtjni.runtime;

public class PointerMath {
   private static final boolean bits32 = Library.getBitModel() == 32;

   public static final long add(long var0, long var2) {
      return bits32 ? (long)((int)(var0 + var2)) : var0 + var2;
   }
}
