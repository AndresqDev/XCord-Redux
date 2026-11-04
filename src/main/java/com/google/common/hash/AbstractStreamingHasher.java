package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@ElementTypesAreNonnullByDefault
@CanIgnoreReturnValue
abstract class AbstractStreamingHasher extends AbstractHasher {
   private final ByteBuffer buffer;
   private final int bufferSize;
   private final int chunkSize;

   protected AbstractStreamingHasher(int var1) {
      this(var1, var1);
   }

   protected AbstractStreamingHasher(int var1, int var2) {
      Preconditions.checkArgument(var2 % var1 == 0);
      this.buffer = ByteBuffer.allocate(var2 + 7).order(ByteOrder.LITTLE_ENDIAN);
      this.bufferSize = var2;
      this.chunkSize = var1;
   }

   protected abstract void process(ByteBuffer var1);

   protected void processRemaining(ByteBuffer var1) {
      Java8Compatibility.position(var1, var1.limit());
      Java8Compatibility.limit(var1, this.chunkSize + 7);

      while (var1.position() < this.chunkSize) {
         var1.putLong(0L);
      }

      Java8Compatibility.limit(var1, this.chunkSize);
      Java8Compatibility.flip(var1);
      this.process(var1);
   }

   @Override
   public final Hasher putBytes(byte[] var1, int var2, int var3) {
      return this.putBytesInternal(ByteBuffer.wrap(var1, var2, var3).order(ByteOrder.LITTLE_ENDIAN));
   }

   @Override
   public final Hasher putBytes(ByteBuffer var1) {
      ByteOrder var2 = var1.order();

      Hasher var3;
      try {
         var1.order(ByteOrder.LITTLE_ENDIAN);
         var3 = this.putBytesInternal(var1);
      } finally {
         var1.order(var2);
      }

      return var3;
   }

   private Hasher putBytesInternal(ByteBuffer var1) {
      if (var1.remaining() <= this.buffer.remaining()) {
         this.buffer.put(var1);
         this.munchIfFull();
         return this;
      } else {
         int var2 = this.bufferSize - this.buffer.position();

         for (int var3 = 0; var3 < var2; var3++) {
            this.buffer.put(var1.get());
         }

         this.munch();

         while (var1.remaining() >= this.chunkSize) {
            this.process(var1);
         }

         this.buffer.put(var1);
         return this;
      }
   }

   @Override
   public final Hasher putByte(byte var1) {
      this.buffer.put(var1);
      this.munchIfFull();
      return this;
   }

   @Override
   public final Hasher putShort(short var1) {
      this.buffer.putShort(var1);
      this.munchIfFull();
      return this;
   }

   @Override
   public final Hasher putChar(char var1) {
      this.buffer.putChar(var1);
      this.munchIfFull();
      return this;
   }

   @Override
   public final Hasher putInt(int var1) {
      this.buffer.putInt(var1);
      this.munchIfFull();
      return this;
   }

   @Override
   public final Hasher putLong(long var1) {
      this.buffer.putLong(var1);
      this.munchIfFull();
      return this;
   }

   @Override
   public final HashCode hash() {
      this.munch();
      Java8Compatibility.flip(this.buffer);
      if (this.buffer.remaining() > 0) {
         this.processRemaining(this.buffer);
         Java8Compatibility.position(this.buffer, this.buffer.limit());
      }

      return this.makeHash();
   }

   protected abstract HashCode makeHash();

   private void munchIfFull() {
      if (this.buffer.remaining() < 8) {
         this.munch();
      }
   }

   private void munch() {
      Java8Compatibility.flip(this.buffer);

      while (this.buffer.remaining() >= this.chunkSize) {
         this.process(this.buffer);
      }

      this.buffer.compact();
   }
}
