package io.netty.buffer;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public final class Unpooled {
   private static final ByteBufAllocator ALLOC = UnpooledByteBufAllocator.DEFAULT;
   public static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
   public static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
   public static final ByteBuf EMPTY_BUFFER = ALLOC.buffer(0, 0);

   public static ByteBuf buffer() {
      return ALLOC.heapBuffer();
   }

   public static ByteBuf directBuffer() {
      return ALLOC.directBuffer();
   }

   public static ByteBuf buffer(int var0) {
      return ALLOC.heapBuffer(var0);
   }

   public static ByteBuf directBuffer(int var0) {
      return ALLOC.directBuffer(var0);
   }

   public static ByteBuf buffer(int var0, int var1) {
      return ALLOC.heapBuffer(var0, var1);
   }

   public static ByteBuf directBuffer(int var0, int var1) {
      return ALLOC.directBuffer(var0, var1);
   }

   public static ByteBuf wrappedBuffer(byte[] var0) {
      return (ByteBuf)(var0.length == 0 ? EMPTY_BUFFER : new UnpooledHeapByteBuf(ALLOC, var0, var0.length));
   }

   public static ByteBuf wrappedBuffer(byte[] var0, int var1, int var2) {
      if (var2 == 0) {
         return EMPTY_BUFFER;
      } else {
         return var1 == 0 && var2 == var0.length ? wrappedBuffer(var0) : wrappedBuffer(var0).slice(var1, var2);
      }
   }

   public static ByteBuf wrappedBuffer(ByteBuffer var0) {
      if (!var0.hasRemaining()) {
         return EMPTY_BUFFER;
      } else if (!var0.isDirect() && var0.hasArray()) {
         return wrappedBuffer(var0.array(), var0.arrayOffset() + var0.position(), var0.remaining()).order(var0.order());
      } else if (PlatformDependent.hasUnsafe()) {
         if (var0.isReadOnly()) {
            return (ByteBuf)(var0.isDirect() ? new ReadOnlyUnsafeDirectByteBuf(ALLOC, var0) : new ReadOnlyByteBufferBuf(ALLOC, var0));
         } else {
            return new UnpooledUnsafeDirectByteBuf(ALLOC, var0, var0.remaining());
         }
      } else {
         return (ByteBuf)(var0.isReadOnly() ? new ReadOnlyByteBufferBuf(ALLOC, var0) : new UnpooledDirectByteBuf(ALLOC, var0, var0.remaining()));
      }
   }

   public static ByteBuf wrappedBuffer(long var0, int var2, boolean var3) {
      return new WrappedUnpooledUnsafeDirectByteBuf(ALLOC, var0, var2, var3);
   }

   public static ByteBuf wrappedBuffer(ByteBuf var0) {
      if (var0.isReadable()) {
         return var0.slice();
      } else {
         var0.release();
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf wrappedBuffer(byte[]... var0) {
      return wrappedBuffer(var0.length, var0);
   }

   public static ByteBuf wrappedBuffer(ByteBuf... var0) {
      return wrappedBuffer(var0.length, var0);
   }

   public static ByteBuf wrappedBuffer(ByteBuffer... var0) {
      return wrappedBuffer(var0.length, var0);
   }

   static <T> ByteBuf wrappedBuffer(int var0, CompositeByteBuf.ByteWrapper<T> var1, T[] var2) {
      switch (var2.length) {
         case 0:
            break;
         case 1:
            if (!var1.isEmpty(var2[0])) {
               return var1.wrap(var2[0]);
            }
            break;
         default:
            int var3 = 0;

            for (int var4 = var2.length; var3 < var4; var3++) {
               Object var5 = var2[var3];
               if (var5 == null) {
                  return EMPTY_BUFFER;
               }

               if (!var1.isEmpty(var5)) {
                  return new CompositeByteBuf(ALLOC, false, var0, var1, var2, var3);
               }
            }
      }

      return EMPTY_BUFFER;
   }

   public static ByteBuf wrappedBuffer(int var0, byte[]... var1) {
      return wrappedBuffer(var0, CompositeByteBuf.BYTE_ARRAY_WRAPPER, var1);
   }

   public static ByteBuf wrappedBuffer(int var0, ByteBuf... var1) {
      switch (var1.length) {
         case 0:
            break;
         case 1:
            ByteBuf var2 = var1[0];
            if (var2.isReadable()) {
               return wrappedBuffer(var2.order(BIG_ENDIAN));
            }

            var2.release();
            break;
         default:
            for (int var3 = 0; var3 < var1.length; var3++) {
               ByteBuf var4 = var1[var3];
               if (var4.isReadable()) {
                  return new CompositeByteBuf(ALLOC, false, var0, var1, var3);
               }

               var4.release();
            }
      }

      return EMPTY_BUFFER;
   }

   public static ByteBuf wrappedBuffer(int var0, ByteBuffer... var1) {
      return wrappedBuffer(var0, CompositeByteBuf.BYTE_BUFFER_WRAPPER, var1);
   }

   public static CompositeByteBuf compositeBuffer() {
      return compositeBuffer(16);
   }

   public static CompositeByteBuf compositeBuffer(int var0) {
      return new CompositeByteBuf(ALLOC, false, var0);
   }

   public static ByteBuf copiedBuffer(byte[] var0) {
      return var0.length == 0 ? EMPTY_BUFFER : wrappedBuffer((byte[])var0.clone());
   }

   public static ByteBuf copiedBuffer(byte[] var0, int var1, int var2) {
      if (var2 == 0) {
         return EMPTY_BUFFER;
      } else {
         byte[] var3 = PlatformDependent.allocateUninitializedArray(var2);
         System.arraycopy(var0, var1, var3, 0, var2);
         return wrappedBuffer(var3);
      }
   }

   public static ByteBuf copiedBuffer(ByteBuffer var0) {
      int var1 = var0.remaining();
      if (var1 == 0) {
         return EMPTY_BUFFER;
      } else {
         byte[] var2 = PlatformDependent.allocateUninitializedArray(var1);
         ByteBuffer var3 = var0.duplicate();
         var3.get(var2);
         return wrappedBuffer(var2).order(var3.order());
      }
   }

   public static ByteBuf copiedBuffer(ByteBuf var0) {
      int var1 = var0.readableBytes();
      if (var1 > 0) {
         ByteBuf var2 = buffer(var1);
         var2.writeBytes(var0, var0.readerIndex(), var1);
         return var2;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copiedBuffer(byte[]... var0) {
      switch (var0.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            if (var0[0].length == 0) {
               return EMPTY_BUFFER;
            }

            return copiedBuffer(var0[0]);
         default:
            int var1 = 0;

            for (byte[] var5 : var0) {
               if (Integer.MAX_VALUE - var1 < var5.length) {
                  throw new IllegalArgumentException("The total length of the specified arrays is too big.");
               }

               var1 += var5.length;
            }

            if (var1 == 0) {
               return EMPTY_BUFFER;
            } else {
               byte[] var6 = PlatformDependent.allocateUninitializedArray(var1);
               int var7 = 0;

               for (int var8 = 0; var7 < var0.length; var7++) {
                  byte[] var9 = var0[var7];
                  System.arraycopy(var9, 0, var6, var8, var9.length);
                  var8 += var9.length;
               }

               return wrappedBuffer(var6);
            }
      }
   }

   public static ByteBuf copiedBuffer(ByteBuf... var0) {
      switch (var0.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            return copiedBuffer(var0[0]);
         default:
            ByteOrder var1 = null;
            int var2 = 0;

            for (ByteBuf var6 : var0) {
               int var7 = var6.readableBytes();
               if (var7 > 0) {
                  if (Integer.MAX_VALUE - var2 < var7) {
                     throw new IllegalArgumentException("The total length of the specified buffers is too big.");
                  }

                  var2 += var7;
                  if (var1 != null) {
                     if (!var1.equals(var6.order())) {
                        throw new IllegalArgumentException("inconsistent byte order");
                     }
                  } else {
                     var1 = var6.order();
                  }
               }
            }

            if (var2 == 0) {
               return EMPTY_BUFFER;
            } else {
               byte[] var8 = PlatformDependent.allocateUninitializedArray(var2);
               int var9 = 0;

               for (int var10 = 0; var9 < var0.length; var9++) {
                  ByteBuf var11 = var0[var9];
                  int var12 = var11.readableBytes();
                  var11.getBytes(var11.readerIndex(), var8, var10, var12);
                  var10 += var12;
               }

               return wrappedBuffer(var8).order(var1);
            }
      }
   }

   public static ByteBuf copiedBuffer(ByteBuffer... var0) {
      switch (var0.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            return copiedBuffer(var0[0]);
         default:
            ByteOrder var1 = null;
            int var2 = 0;

            for (ByteBuffer var6 : var0) {
               int var7 = var6.remaining();
               if (var7 > 0) {
                  if (Integer.MAX_VALUE - var2 < var7) {
                     throw new IllegalArgumentException("The total length of the specified buffers is too big.");
                  }

                  var2 += var7;
                  if (var1 != null) {
                     if (!var1.equals(var6.order())) {
                        throw new IllegalArgumentException("inconsistent byte order");
                     }
                  } else {
                     var1 = var6.order();
                  }
               }
            }

            if (var2 == 0) {
               return EMPTY_BUFFER;
            } else {
               byte[] var8 = PlatformDependent.allocateUninitializedArray(var2);
               int var9 = 0;

               for (int var10 = 0; var9 < var0.length; var9++) {
                  ByteBuffer var11 = var0[var9].duplicate();
                  int var12 = var11.remaining();
                  var11.get(var8, var10, var12);
                  var10 += var12;
               }

               return wrappedBuffer(var8).order(var1);
            }
      }
   }

   public static ByteBuf copiedBuffer(CharSequence var0, Charset var1) {
      ObjectUtil.checkNotNull(var0, "string");
      if (CharsetUtil.UTF_8.equals(var1)) {
         return copiedBufferUtf8(var0);
      } else if (CharsetUtil.US_ASCII.equals(var1)) {
         return copiedBufferAscii(var0);
      } else {
         return var0 instanceof CharBuffer ? copiedBuffer((CharBuffer)var0, var1) : copiedBuffer(CharBuffer.wrap(var0), var1);
      }
   }

   private static ByteBuf copiedBufferUtf8(CharSequence var0) {
      boolean var1 = true;
      ByteBuf var2 = ALLOC.heapBuffer(ByteBufUtil.utf8Bytes(var0));

      ByteBuf var3;
      try {
         ByteBufUtil.writeUtf8(var2, var0);
         var1 = false;
         var3 = var2;
      } finally {
         if (var1) {
            var2.release();
         }
      }

      return var3;
   }

   private static ByteBuf copiedBufferAscii(CharSequence var0) {
      boolean var1 = true;
      ByteBuf var2 = ALLOC.heapBuffer(var0.length());

      ByteBuf var3;
      try {
         ByteBufUtil.writeAscii(var2, var0);
         var1 = false;
         var3 = var2;
      } finally {
         if (var1) {
            var2.release();
         }
      }

      return var3;
   }

   public static ByteBuf copiedBuffer(CharSequence var0, int var1, int var2, Charset var3) {
      ObjectUtil.checkNotNull(var0, "string");
      if (var2 == 0) {
         return EMPTY_BUFFER;
      } else if (var0 instanceof CharBuffer) {
         CharBuffer var4 = (CharBuffer)var0;
         if (var4.hasArray()) {
            return copiedBuffer(var4.array(), var4.arrayOffset() + var4.position() + var1, var2, var3);
         } else {
            var4 = var4.slice();
            ((Buffer)var4).limit(var2);
            ((Buffer)var4).position(var1);
            return copiedBuffer(var4, var3);
         }
      } else {
         return copiedBuffer(CharBuffer.wrap(var0, var1, var1 + var2), var3);
      }
   }

   public static ByteBuf copiedBuffer(char[] var0, Charset var1) {
      ObjectUtil.checkNotNull(var0, "array");
      return copiedBuffer(var0, 0, var0.length, var1);
   }

   public static ByteBuf copiedBuffer(char[] var0, int var1, int var2, Charset var3) {
      ObjectUtil.checkNotNull(var0, "array");
      return var2 == 0 ? EMPTY_BUFFER : copiedBuffer(CharBuffer.wrap(var0, var1, var2), var3);
   }

   private static ByteBuf copiedBuffer(CharBuffer var0, Charset var1) {
      return ByteBufUtil.encodeString0(ALLOC, true, var0, var1, 0);
   }

   @Deprecated
   public static ByteBuf unmodifiableBuffer(ByteBuf var0) {
      ByteOrder var1 = var0.order();
      return (ByteBuf)(var1 == BIG_ENDIAN ? new ReadOnlyByteBuf(var0) : new ReadOnlyByteBuf(var0.order(BIG_ENDIAN)).order(LITTLE_ENDIAN));
   }

   public static ByteBuf copyInt(int var0) {
      ByteBuf var1 = buffer(4);
      var1.writeInt(var0);
      return var1;
   }

   public static ByteBuf copyInt(int... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 4);

         for (int var5 : var0) {
            var1.writeInt(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyShort(int var0) {
      ByteBuf var1 = buffer(2);
      var1.writeShort(var0);
      return var1;
   }

   public static ByteBuf copyShort(short... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 2);

         for (short var5 : var0) {
            var1.writeShort(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyShort(int... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 2);

         for (int var5 : var0) {
            var1.writeShort(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyMedium(int var0) {
      ByteBuf var1 = buffer(3);
      var1.writeMedium(var0);
      return var1;
   }

   public static ByteBuf copyMedium(int... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 3);

         for (int var5 : var0) {
            var1.writeMedium(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyLong(long var0) {
      ByteBuf var2 = buffer(8);
      var2.writeLong(var0);
      return var2;
   }

   public static ByteBuf copyLong(long... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 8);

         for (long var5 : var0) {
            var1.writeLong(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyBoolean(boolean var0) {
      ByteBuf var1 = buffer(1);
      var1.writeBoolean(var0);
      return var1;
   }

   public static ByteBuf copyBoolean(boolean... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length);

         for (boolean var5 : var0) {
            var1.writeBoolean(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyFloat(float var0) {
      ByteBuf var1 = buffer(4);
      var1.writeFloat(var0);
      return var1;
   }

   public static ByteBuf copyFloat(float... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 4);

         for (float var5 : var0) {
            var1.writeFloat(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf copyDouble(double var0) {
      ByteBuf var2 = buffer(8);
      var2.writeDouble(var0);
      return var2;
   }

   public static ByteBuf copyDouble(double... var0) {
      if (var0 != null && var0.length != 0) {
         ByteBuf var1 = buffer(var0.length * 8);

         for (double var5 : var0) {
            var1.writeDouble(var5);
         }

         return var1;
      } else {
         return EMPTY_BUFFER;
      }
   }

   public static ByteBuf unreleasableBuffer(ByteBuf var0) {
      return new UnreleasableByteBuf(var0);
   }

   @Deprecated
   public static ByteBuf unmodifiableBuffer(ByteBuf... var0) {
      return wrappedUnmodifiableBuffer(true, var0);
   }

   public static ByteBuf wrappedUnmodifiableBuffer(ByteBuf... var0) {
      return wrappedUnmodifiableBuffer(false, var0);
   }

   private static ByteBuf wrappedUnmodifiableBuffer(boolean var0, ByteBuf... var1) {
      switch (var1.length) {
         case 0:
            return EMPTY_BUFFER;
         case 1:
            return var1[0].asReadOnly();
         default:
            if (var0) {
               var1 = Arrays.copyOf(var1, var1.length, ByteBuf[].class);
            }

            return new FixedCompositeByteBuf(ALLOC, var1);
      }
   }

   private Unpooled() {
   }

   static {
      assert EMPTY_BUFFER instanceof EmptyByteBuf : "EMPTY_BUFFER must be an EmptyByteBuf.";
   }
}
