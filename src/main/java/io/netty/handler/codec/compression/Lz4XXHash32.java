package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

public final class Lz4XXHash32 extends ByteBufChecksum {
   private static final XXHash32 XXHASH32 = XXHashFactory.fastestInstance().hash32();
   private final int seed;
   private boolean used;
   private int value;

   public Lz4XXHash32(int var1) {
      this.seed = var1;
   }

   @Override
   public void update(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (this.used) {
         throw new IllegalStateException();
      } else {
         this.value = XXHASH32.hash(var1, var2, var3, this.seed);
         this.used = true;
      }
   }

   @Override
   public void update(ByteBuf var1, int var2, int var3) {
      if (this.used) {
         throw new IllegalStateException();
      } else {
         if (var1.hasArray()) {
            this.value = XXHASH32.hash(var1.array(), var1.arrayOffset() + var2, var3, this.seed);
         } else {
            this.value = XXHASH32.hash(CompressionUtil.safeNioBuffer(var1, var2, var3), this.seed);
         }

         this.used = true;
      }
   }

   @Override
   public long getValue() {
      if (!this.used) {
         throw new IllegalStateException();
      } else {
         return (long)this.value & 268435455L;
      }
   }

   @Override
   public void reset() {
      this.used = false;
   }
}
