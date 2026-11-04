package org.apache.http;

public class HttpException extends Exception {
   private static final int FIRST_VALID_CHAR = 32;
   private static final long serialVersionUID = -5437299376222011036L;

   static String clean(String var0) {
      char[] var1 = var0.toCharArray();
      int var2 = 0;

      while (var2 < var1.length && var1[var2] >= ' ') {
         var2++;
      }

      if (var2 == var1.length) {
         return var0;
      } else {
         StringBuilder var3 = new StringBuilder(var1.length * 2);

         for (int var6 = 0; var6 < var1.length; var6++) {
            char var4 = var1[var6];
            if (var4 < ' ') {
               var3.append("[0x");
               String var5 = Integer.toHexString(var6);
               if (var5.length() == 1) {
                  var3.append("0");
               }

               var3.append(var5);
               var3.append("]");
            } else {
               var3.append(var4);
            }
         }

         return var3.toString();
      }
   }

   public HttpException() {
   }

   public HttpException(String var1) {
      super(clean(var1));
   }

   public HttpException(String var1, Throwable var2) {
      super(clean(var1));
      this.initCause(var2);
   }
}
