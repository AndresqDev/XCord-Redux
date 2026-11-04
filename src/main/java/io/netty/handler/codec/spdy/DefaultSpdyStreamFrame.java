package io.netty.handler.codec.spdy;

import io.netty.util.internal.ObjectUtil;

public abstract class DefaultSpdyStreamFrame implements SpdyStreamFrame {
   private int streamId;
   private boolean last;

   protected DefaultSpdyStreamFrame(int var1) {
      this.setStreamId(var1);
   }

   @Override
   public int streamId() {
      return this.streamId;
   }

   @Override
   public SpdyStreamFrame setStreamId(int var1) {
      ObjectUtil.checkPositive(var1, "streamId");
      this.streamId = var1;
      return this;
   }

   @Override
   public boolean isLast() {
      return this.last;
   }

   @Override
   public SpdyStreamFrame setLast(boolean var1) {
      this.last = var1;
      return this;
   }
}
