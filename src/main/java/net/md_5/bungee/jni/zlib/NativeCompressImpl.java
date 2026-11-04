package net.md_5.bungee.jni.zlib;

public class NativeCompressImpl {
   int consumed;
   boolean finished;

   static native void initFields();

   native void end(long var1, boolean var3);

   native void reset(long var1, boolean var3);

   native long init(boolean var1, int var2);

   native int process(long var1, long var3, int var5, long var6, int var8, boolean var9);

   static {
      initFields();
   }
}
