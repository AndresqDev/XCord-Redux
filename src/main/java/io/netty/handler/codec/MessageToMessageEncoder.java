package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I> extends ChannelOutboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageEncoder() {
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
   }

   protected MessageToMessageEncoder(Class<? extends I> var1) {
      this.matcher = TypeParameterMatcher.get(var1);
   }

   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return this.matcher.match(var1);
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      CodecOutputList var4 = null;

      try {
         if (this.acceptOutboundMessage(var2)) {
            var4 = CodecOutputList.newInstance();
            Object var5 = var2;

            try {
               this.encode(var1, (I)var5, var4);
            } catch (Throwable var30) {
               ReferenceCountUtil.safeRelease(var2);
               PlatformDependent.throwException(var30);
            }

            ReferenceCountUtil.release(var2);
            if (var4.isEmpty()) {
               throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
            }
         } else {
            var1.write(var2, var3);
         }
      } catch (EncoderException var31) {
         throw var31;
      } catch (Throwable var32) {
         throw new EncoderException(var32);
      } finally {
         if (var4 != null) {
            try {
               int var9 = var4.size() - 1;
               if (var9 == 0) {
                  var1.write(var4.getUnsafe(0), var3);
               } else if (var9 > 0) {
                  if (var3 == var1.voidPromise()) {
                     writeVoidPromise(var1, var4);
                  } else {
                     writePromiseCombiner(var1, var4, var3);
                  }
               }
            } finally {
               var4.recycle();
            }
         }
      }
   }

   private static void writeVoidPromise(ChannelHandlerContext var0, CodecOutputList var1) {
      ChannelPromise var2 = var0.voidPromise();

      for (int var3 = 0; var3 < var1.size(); var3++) {
         var0.write(var1.getUnsafe(var3), var2);
      }
   }

   private static void writePromiseCombiner(ChannelHandlerContext var0, CodecOutputList var1, ChannelPromise var2) {
      PromiseCombiner var3 = new PromiseCombiner(var0.executor());

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3.add(var0.write(var1.getUnsafe(var4)));
      }

      var3.finish(var2);
   }

   protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
