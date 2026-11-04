package com.sun.jna;

import java.nio.CharBuffer;

class NativeString implements CharSequence, Comparable {
   static final String WIDE_STRING = "--WIDE-STRING--";
   private Pointer pointer;
   private String encoding;

   public NativeString(String var1) {
      this(var1, Native.getDefaultStringEncoding());
   }

   public NativeString(String var1, boolean var2) {
      this(var1, var2 ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
   }

   public NativeString(WString var1) {
      this(var1.toString(), "--WIDE-STRING--");
   }

   public NativeString(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("String must not be null");
      } else {
         this.encoding = var2;
         if ("--WIDE-STRING--".equals(this.encoding)) {
            int var3 = (var1.length() + 1) * Native.WCHAR_SIZE;
            this.pointer = new NativeString.StringMemory((long)var3);
            this.pointer.setWideString(0L, var1);
         } else {
            byte[] var4 = Native.getBytes(var1, var2);
            this.pointer = new NativeString.StringMemory((long)(var4.length + 1));
            this.pointer.write(0L, var4, 0, var4.length);
            this.pointer.setByte((long)var4.length, (byte)0);
         }
      }
   }

   @Override
   public int hashCode() {
      return this.toString().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof CharSequence ? this.compareTo(var1) == 0 : false;
   }

   @Override
   public String toString() {
      boolean var1 = "--WIDE-STRING--".equals(this.encoding);
      String var2 = var1 ? "const wchar_t*" : "const char*";
      return var2 + "(" + (var1 ? this.pointer.getWideString(0L) : this.pointer.getString(0L, this.encoding)) + ")";
   }

   public Pointer getPointer() {
      return this.pointer;
   }

   @Override
   public char charAt(int var1) {
      return this.toString().charAt(var1);
   }

   @Override
   public int length() {
      return this.toString().length();
   }

   @Override
   public CharSequence subSequence(int var1, int var2) {
      return CharBuffer.wrap(this.toString()).subSequence(var1, var2);
   }

   @Override
   public int compareTo(Object var1) {
      return var1 == null ? 1 : this.toString().compareTo(var1.toString());
   }

   private class StringMemory extends Memory {
      public StringMemory(long var2) {
         super(var2);
      }

      @Override
      public String toString() {
         return NativeString.this.toString();
      }
   }
}
