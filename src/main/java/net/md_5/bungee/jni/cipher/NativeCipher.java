package net.md_5.bungee.jni.cipher;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

public class NativeCipher implements BungeeCipher {
   private final NativeCipherImpl nativeCipher = new NativeCipherImpl();
   private long ctx;

   @Override
   public void init(boolean var1, SecretKey var2) throws GeneralSecurityException {
      Preconditions.checkArgument(var2.getEncoded().length == 16, "Invalid key size");
      this.free();
      this.ctx = this.nativeCipher.init(var1, var2.getEncoded());
   }

   @Override
   public void free() {
      if (this.ctx != 0L) {
         this.nativeCipher.free(this.ctx);
         this.ctx = 0L;
      }
   }

   @Override
   public void cipher(ByteBuf var1, ByteBuf var2) throws GeneralSecurityException {
      var1.memoryAddress();
      var2.memoryAddress();
      Preconditions.checkState(this.ctx != 0L, "Invalid pointer to AES key!");
      int var3 = var1.readableBytes();
      if (var3 > 0) {
         var2.ensureWritable(var3);
         this.nativeCipher.cipher(this.ctx, var1.memoryAddress() + (long)var1.readerIndex(), var2.memoryAddress() + (long)var2.writerIndex(), var3);
         var1.readerIndex(var1.writerIndex());
         var2.writerIndex(var2.writerIndex() + var3);
      }
   }

   @Override
   public ByteBuf cipher(ChannelHandlerContext var1, ByteBuf var2) throws GeneralSecurityException {
      int var3 = var2.readableBytes();
      ByteBuf var4 = var1.alloc().directBuffer(var3);
      this.cipher(var2, var4);
      return var4;
   }

   public NativeCipherImpl getNativeCipher() {
      return this.nativeCipher;
   }
}
