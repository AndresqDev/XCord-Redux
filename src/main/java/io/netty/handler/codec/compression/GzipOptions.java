package io.netty.handler.codec.compression;

public final class GzipOptions extends DeflateOptions {
   static final GzipOptions DEFAULT = new GzipOptions(6, 15, 8);

   GzipOptions(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }
}
