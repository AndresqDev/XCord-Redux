package com.google.protobuf;

import java.nio.Buffer;
import java.nio.ByteBuffer;

abstract class AllocatedBuffer {
   public abstract boolean hasNioBuffer();

   public abstract boolean hasArray();

   public abstract ByteBuffer nioBuffer();

   public abstract byte[] array();

   public abstract int arrayOffset();

   public abstract int position();

   public abstract AllocatedBuffer position(int var1);

   public abstract int limit();

   public abstract int remaining();

   public static AllocatedBuffer wrap(byte[] var0) {
      return wrapNoCheck(var0, 0, var0.length);
   }

   public static AllocatedBuffer wrap(byte[] var0, int var1, int var2) {
      if (var1 >= 0 && var2 >= 0 && var1 + var2 <= var0.length) {
         return wrapNoCheck(var0, var1, var2);
      } else {
         throw new IndexOutOfBoundsException(String.format("bytes.length=%d, offset=%d, length=%d", var0.length, var1, var2));
      }
   }

   public static AllocatedBuffer wrap(final ByteBuffer var0) {
      Internal.checkNotNull(var0, "buffer");
      return new AllocatedBuffer() {
         @Override
         public boolean hasNioBuffer() {
            return true;
         }

         @Override
         public ByteBuffer nioBuffer() {
            return var0;
         }

         @Override
         public boolean hasArray() {
            return var0.hasArray();
         }

         @Override
         public byte[] array() {
            return var0.array();
         }

         @Override
         public int arrayOffset() {
            return var0.arrayOffset();
         }

         @Override
         public int position() {
            return var0.position();
         }

         @Override
         public AllocatedBuffer position(int var1) {
            ((Buffer)var0).position(var1);
            return this;
         }

         @Override
         public int limit() {
            return var0.limit();
         }

         @Override
         public int remaining() {
            return var0.remaining();
         }
      };
   }

   private static AllocatedBuffer wrapNoCheck(final byte[] var0, final int var1, final int var2) {
      return new AllocatedBuffer() {
         private int position;

         @Override
         public boolean hasNioBuffer() {
            return false;
         }

         @Override
         public ByteBuffer nioBuffer() {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean hasArray() {
            return true;
         }

         @Override
         public byte[] array() {
            return var0;
         }

         @Override
         public int arrayOffset() {
            return var1;
         }

         @Override
         public int position() {
            return this.position;
         }

         @Override
         public AllocatedBuffer position(int var1x) {
            if (var1x >= 0 && var1x <= var2) {
               this.position = var1x;
               return this;
            } else {
               throw new IllegalArgumentException("Invalid position: " + var1x);
            }
         }

         @Override
         public int limit() {
            return var2;
         }

         @Override
         public int remaining() {
            return var2 - this.position;
         }
      };
   }
}
