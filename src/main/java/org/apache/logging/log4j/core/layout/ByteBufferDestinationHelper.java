package org.apache.logging.log4j.core.layout;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class ByteBufferDestinationHelper {
   private ByteBufferDestinationHelper() {
   }

   public static void writeToUnsynchronized(ByteBuffer var0, ByteBufferDestination var1) {
      ByteBuffer var2;
      for (var2 = var1.getByteBuffer(); var0.remaining() > var2.remaining(); var2 = var1.drain(var2)) {
         int var3 = var0.limit();
         ((Buffer)var0).limit(Math.min(var0.limit(), var0.position() + var2.remaining()));
         var2.put(var0);
         ((Buffer)var0).limit(var3);
      }

      var2.put(var0);
   }

   public static void writeToUnsynchronized(byte[] var0, int var1, int var2, ByteBufferDestination var3) {
      ByteBuffer var4;
      for (var4 = var3.getByteBuffer(); var2 > var4.remaining(); var4 = var3.drain(var4)) {
         int var5 = var4.remaining();
         var4.put(var0, var1, var5);
         var1 += var5;
         var2 -= var5;
      }

      var4.put(var0, var1, var2);
   }
}
