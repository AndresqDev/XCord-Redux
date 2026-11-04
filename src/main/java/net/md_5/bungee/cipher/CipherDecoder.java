package net.md_5.bungee.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final BungeeCipher cipher;

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(this.cipher.cipher(var1, var2));
   }

   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cipher.free();
   }

   public CipherDecoder(BungeeCipher var1) {
      this.cipher = var1;
   }
}
