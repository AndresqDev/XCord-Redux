package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;

public final class RawUnixChannelOption extends GenericUnixChannelOption<ByteBuffer> {
   private final int length;

   public RawUnixChannelOption(String var1, int var2, int var3, int var4) {
      super(var1, var2, var3);
      this.length = ObjectUtil.checkPositive(var4, "length");
   }

   public int length() {
      return this.length;
   }

   public void validate(ByteBuffer var1) {
      super.validate(var1);
      if (var1.remaining() != this.length) {
         throw new IllegalArgumentException("Length of value does not match. Expected " + this.length + ", but got " + var1.remaining());
      }
   }
}
