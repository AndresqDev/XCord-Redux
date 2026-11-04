package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.ObjectUtil;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class CompatibleObjectEncoder extends MessageToByteEncoder<Serializable> {
   private final int resetInterval;
   private int writtenObjects;

   public CompatibleObjectEncoder() {
      this(16);
   }

   public CompatibleObjectEncoder(int var1) {
      this.resetInterval = ObjectUtil.checkPositiveOrZero(var1, "resetInterval");
   }

   protected ObjectOutputStream newObjectOutputStream(OutputStream var1) throws Exception {
      return new ObjectOutputStream(var1);
   }

   protected void encode(ChannelHandlerContext var1, Serializable var2, ByteBuf var3) throws Exception {
      ObjectOutputStream var4 = this.newObjectOutputStream(new ByteBufOutputStream(var3));

      try {
         if (this.resetInterval != 0) {
            this.writtenObjects++;
            if (this.writtenObjects % this.resetInterval == 0) {
               var4.reset();
            }
         }

         var4.writeObject(var2);
         var4.flush();
      } finally {
         var4.close();
      }
   }
}
