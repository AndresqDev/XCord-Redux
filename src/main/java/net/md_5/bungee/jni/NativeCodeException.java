package net.md_5.bungee.jni;

public class NativeCodeException extends Exception {
   public NativeCodeException(String var1, int var2) {
      super(var1 + " : " + var2);
   }
}
