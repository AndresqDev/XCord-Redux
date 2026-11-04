package org.fusesource.hawtjni.runtime;

public class Callback {
   Object object;
   String method;
   String signature;
   int argCount;
   long address;
   long errorResult;
   boolean isStatic;
   boolean isArrayBased;
   static final String PTR_SIGNATURE = "J";
   static final String SIGNATURE_0 = getSignature(0);
   static final String SIGNATURE_1 = getSignature(1);
   static final String SIGNATURE_2 = getSignature(2);
   static final String SIGNATURE_3 = getSignature(3);
   static final String SIGNATURE_4 = getSignature(4);
   static final String SIGNATURE_N = "([J)J";

   public Callback(Object var1, String var2, int var3) {
      this(var1, var2, var3, false);
   }

   public Callback(Object var1, String var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, 0L);
   }

   public Callback(Object var1, String var2, int var3, boolean var4, long var5) {
      this.object = var1;
      this.method = var2;
      this.argCount = var3;
      this.isStatic = var1 instanceof Class;
      this.isArrayBased = var4;
      this.errorResult = var5;
      if (var4) {
         this.signature = "([J)J";
      } else {
         switch (var3) {
            case 0:
               this.signature = SIGNATURE_0;
               break;
            case 1:
               this.signature = SIGNATURE_1;
               break;
            case 2:
               this.signature = SIGNATURE_2;
               break;
            case 3:
               this.signature = SIGNATURE_3;
               break;
            case 4:
               this.signature = SIGNATURE_4;
               break;
            default:
               this.signature = getSignature(var3);
         }
      }

      this.address = bind(this, var1, var2, this.signature, var3, this.isStatic, var4, var5);
   }

   static synchronized native long bind(Callback var0, Object var1, String var2, String var3, int var4, boolean var5, boolean var6, long var7);

   public void dispose() {
      if (this.object != null) {
         unbind(this);
         this.object = this.method = this.signature = null;
         this.address = 0L;
      }
   }

   public long getAddress() {
      return this.address;
   }

   public static native String getPlatform();

   public static native int getEntryCount();

   static String getSignature(int var0) {
      String var1 = "(";

      for (int var2 = 0; var2 < var0; var2++) {
         var1 = var1 + "J";
      }

      return var1 + ")J";
   }

   public static final synchronized native void setEnabled(boolean var0);

   public static final synchronized native boolean getEnabled();

   public static final synchronized native void reset();

   static final synchronized native void unbind(Callback var0);
}
