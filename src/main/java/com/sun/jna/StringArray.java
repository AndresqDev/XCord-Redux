package com.sun.jna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringArray extends Memory implements Function.PostCallRead {
   private String encoding;
   private List<NativeString> natives = new ArrayList<>();
   private Object[] original;

   public StringArray(String[] var1) {
      this(var1, false);
   }

   public StringArray(String[] var1, boolean var2) {
      this(var1, var2 ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
   }

   public StringArray(String[] var1, String var2) {
      this(var1, var2);
   }

   public StringArray(WString[] var1) {
      this(var1, "--WIDE-STRING--");
   }

   private StringArray(Object[] var1, String var2) {
      super((long)((var1.length + 1) * Pointer.SIZE));
      this.original = var1;
      this.encoding = var2;

      for (int var3 = 0; var3 < var1.length; var3++) {
         Pointer var4 = null;
         if (var1[var3] != null) {
            NativeString var5 = new NativeString(var1[var3].toString(), var2);
            this.natives.add(var5);
            var4 = var5.getPointer();
         }

         this.setPointer((long)(Pointer.SIZE * var3), var4);
      }

      this.setPointer((long)(Pointer.SIZE * var1.length), null);
   }

   @Override
   public void read() {
      boolean var1 = this.original instanceof WString[];
      boolean var2 = "--WIDE-STRING--".equals(this.encoding);

      for (int var3 = 0; var3 < this.original.length; var3++) {
         Pointer var4 = this.getPointer((long)(var3 * Pointer.SIZE));
         Object var5 = null;
         if (var4 != null) {
            var5 = var2 ? var4.getWideString(0L) : var4.getString(0L, this.encoding);
            if (var1) {
               var5 = new WString((String)var5);
            }
         }

         this.original[var3] = var5;
      }
   }

   @Override
   public String toString() {
      boolean var1 = "--WIDE-STRING--".equals(this.encoding);
      Object var2 = var1 ? "const wchar_t*[]" : "const char*[]";
      return var2 + Arrays.<Object>asList(this.original);
   }
}
