package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import java.util.Locale;

public abstract class AbstractSniHandler<T> extends SslClientHelloHandler<T> {
   private String hostname;

   private static String extractSniHostname(ByteBuf var0) {
      int var1 = var0.readerIndex();
      int var2 = var0.writerIndex();
      var1 += 34;
      if (var2 - var1 >= 6) {
         short var3 = var0.getUnsignedByte(var1);
         var1 += var3 + 1;
         int var4 = var0.getUnsignedShort(var1);
         var1 += var4 + 2;
         short var5 = var0.getUnsignedByte(var1);
         var1 += var5 + 1;
         int var6 = var0.getUnsignedShort(var1);
         var1 += 2;
         int var7 = var1 + var6;
         if (var7 <= var2) {
            while (var7 - var1 >= 4) {
               int var8 = var0.getUnsignedShort(var1);
               var1 += 2;
               int var9 = var0.getUnsignedShort(var1);
               var1 += 2;
               if (var7 - var1 < var9) {
                  break;
               }

               if (var8 == 0) {
                  var1 += 2;
                  if (var7 - var1 >= 3) {
                     short var10 = var0.getUnsignedByte(var1);
                     var1++;
                     if (var10 == 0) {
                        int var11 = var0.getUnsignedShort(var1);
                        var1 += 2;
                        if (var7 - var1 >= var11) {
                           String var12 = var0.toString(var1, var11, CharsetUtil.US_ASCII);
                           return var12.toLowerCase(Locale.US);
                        }
                     }
                  }
                  break;
               }

               var1 += var9;
            }
         }
      }

      return null;
   }

   @Override
   protected Future<T> lookup(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      this.hostname = var2 == null ? null : extractSniHostname(var2);
      return this.lookup(var1, this.hostname);
   }

   @Override
   protected void onLookupComplete(ChannelHandlerContext var1, Future<T> var2) throws Exception {
      try {
         this.onLookupComplete(var1, this.hostname, var2);
      } finally {
         fireSniCompletionEvent(var1, this.hostname, var2);
      }
   }

   protected abstract Future<T> lookup(ChannelHandlerContext var1, String var2) throws Exception;

   protected abstract void onLookupComplete(ChannelHandlerContext var1, String var2, Future<T> var3) throws Exception;

   private static void fireSniCompletionEvent(ChannelHandlerContext var0, String var1, Future<?> var2) {
      Throwable var3 = var2.cause();
      if (var3 == null) {
         var0.fireUserEventTriggered(new SniCompletionEvent(var1));
      } else {
         var0.fireUserEventTriggered(new SniCompletionEvent(var1, var3));
      }
   }
}
