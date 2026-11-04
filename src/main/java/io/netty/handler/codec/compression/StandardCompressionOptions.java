package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;

public final class StandardCompressionOptions {
   private StandardCompressionOptions() {
   }

   public static BrotliOptions brotli() {
      return BrotliOptions.DEFAULT;
   }

   public static BrotliOptions brotli(Parameters var0) {
      return new BrotliOptions(var0);
   }

   public static ZstdOptions zstd() {
      return ZstdOptions.DEFAULT;
   }

   public static ZstdOptions zstd(int var0, int var1, int var2) {
      return new ZstdOptions(var0, var1, var2);
   }

   public static GzipOptions gzip() {
      return GzipOptions.DEFAULT;
   }

   public static GzipOptions gzip(int var0, int var1, int var2) {
      return new GzipOptions(var0, var1, var2);
   }

   public static DeflateOptions deflate() {
      return DeflateOptions.DEFAULT;
   }

   public static DeflateOptions deflate(int var0, int var1, int var2) {
      return new DeflateOptions(var0, var1, var2);
   }
}
