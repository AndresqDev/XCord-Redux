package net.md_5.bungee.jni.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class JavaCipher implements BungeeCipher {
   private final Cipher cipher;
   private static final ThreadLocal<byte[]> heapInLocal = new JavaCipher.EmptyByteThreadLocal();
   private static final ThreadLocal<byte[]> heapOutLocal = new JavaCipher.EmptyByteThreadLocal();

   public JavaCipher() {
      try {
         this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
      } catch (GeneralSecurityException var2) {
         throw new RuntimeException(var2);
      }
   }

   @Override
   public void init(boolean var1, SecretKey var2) throws GeneralSecurityException {
      int var3 = var1 ? 1 : 2;
      this.cipher.init(var3, var2, new IvParameterSpec(var2.getEncoded()));
   }

   @Override
   public void cipher(ByteBuf var1, ByteBuf var2) throws ShortBufferException {
      int var3 = var1.readableBytes();
      byte[] var4 = this.bufToByte(var1);
      byte[] var5 = heapOutLocal.get();
      int var6 = this.cipher.getOutputSize(var3);
      if (var5.length < var6) {
         var5 = new byte[var6];
         heapOutLocal.set(var5);
      }

      var2.writeBytes(var5, 0, this.cipher.update(var4, 0, var3, var5));
   }

   @Override
   public ByteBuf cipher(ChannelHandlerContext var1, ByteBuf var2) throws ShortBufferException {
      int var3 = var2.readableBytes();
      byte[] var4 = this.bufToByte(var2);
      ByteBuf var5 = var1.alloc().heapBuffer(this.cipher.getOutputSize(var3));
      var5.writerIndex(this.cipher.update(var4, 0, var3, var5.array(), var5.arrayOffset()));
      return var5;
   }

   @Override
   public void free() {
   }

   private byte[] bufToByte(ByteBuf var1) {
      byte[] var2 = heapInLocal.get();
      int var3 = var1.readableBytes();
      if (var2.length < var3) {
         var2 = new byte[var3];
         heapInLocal.set(var2);
      }

      var1.readBytes(var2, 0, var3);
      return var2;
   }

   private static class EmptyByteThreadLocal extends ThreadLocal<byte[]> {
      private EmptyByteThreadLocal() {
      }

      protected byte[] initialValue() {
         return new byte[0];
      }
   }
}
