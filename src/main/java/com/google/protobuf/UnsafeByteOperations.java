package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class UnsafeByteOperations {
   private UnsafeByteOperations() {
   }

   public static ByteString unsafeWrap(byte[] var0) {
      return ByteString.wrap(var0);
   }

   public static ByteString unsafeWrap(byte[] var0, int var1, int var2) {
      return ByteString.wrap(var0, var1, var2);
   }

   public static ByteString unsafeWrap(ByteBuffer var0) {
      return ByteString.wrap(var0);
   }

   public static void unsafeWriteTo(ByteString var0, ByteOutput var1) throws IOException {
      var0.writeTo(var1);
   }
}
