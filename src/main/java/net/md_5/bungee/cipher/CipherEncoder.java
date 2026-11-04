package net.md_5.bungee.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public class CipherEncoder extends MessageToByteEncoder<ByteBuf> {
   private final BungeeCipher cipher;

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      this.cipher.cipher(var2, var3);
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cipher.free();
   }

   public CipherEncoder(BungeeCipher var1) {
      this.cipher = var1;
   }
}
