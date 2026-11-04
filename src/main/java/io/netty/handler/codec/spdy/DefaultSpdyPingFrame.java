package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyPingFrame implements SpdyPingFrame {
   private int id;

   public DefaultSpdyPingFrame(int var1) {
      this.setId(var1);
   }

   @Override
   public int id() {
      return this.id;
   }

   @Override
   public SpdyPingFrame setId(int var1) {
      this.id = var1;
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + StringUtil.NEWLINE + "--> ID = " + this.id();
   }
}
