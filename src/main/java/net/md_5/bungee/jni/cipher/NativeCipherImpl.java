package net.md_5.bungee.jni.cipher;

class NativeCipherImpl {
   native long init(boolean var1, byte[] var2);

   native void free(long var1);

   native void cipher(long var1, long var3, long var5, int var7);
}
