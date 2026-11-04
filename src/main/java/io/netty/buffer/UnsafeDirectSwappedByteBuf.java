package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

final class UnsafeDirectSwappedByteBuf extends AbstractUnsafeSwappedByteBuf {
   UnsafeDirectSwappedByteBuf(AbstractByteBuf var1) {
      super(var1);
   }

   private static long addr(AbstractByteBuf var0, int var1) {
      return var0.memoryAddress() + (long)var1;
   }

   @Override
   protected long _getLong(AbstractByteBuf var1, int var2) {
      return PlatformDependent.getLong(addr(var1, var2));
   }

   @Override
   protected int _getInt(AbstractByteBuf var1, int var2) {
      return PlatformDependent.getInt(addr(var1, var2));
   }

   @Override
   protected short _getShort(AbstractByteBuf var1, int var2) {
      return PlatformDependent.getShort(addr(var1, var2));
   }

   @Override
   protected void _setShort(AbstractByteBuf var1, int var2, short var3) {
      PlatformDependent.putShort(addr(var1, var2), var3);
   }

   @Override
   protected void _setInt(AbstractByteBuf var1, int var2, int var3) {
      PlatformDependent.putInt(addr(var1, var2), var3);
   }

   @Override
   protected void _setLong(AbstractByteBuf var1, int var2, long var3) {
      PlatformDependent.putLong(addr(var1, var2), var3);
   }
}
