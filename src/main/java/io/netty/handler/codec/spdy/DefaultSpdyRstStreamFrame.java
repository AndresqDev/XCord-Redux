package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyRstStreamFrame extends DefaultSpdyStreamFrame implements SpdyRstStreamFrame {
   private SpdyStreamStatus status;

   public DefaultSpdyRstStreamFrame(int var1, int var2) {
      this(var1, SpdyStreamStatus.valueOf(var2));
   }

   public DefaultSpdyRstStreamFrame(int var1, SpdyStreamStatus var2) {
      super(var1);
      this.setStatus(var2);
   }

   @Override
   public SpdyRstStreamFrame setStreamId(int var1) {
      super.setStreamId(var1);
      return this;
   }

   @Override
   public SpdyRstStreamFrame setLast(boolean var1) {
      super.setLast(var1);
      return this;
   }

   @Override
   public SpdyStreamStatus status() {
      return this.status;
   }

   @Override
   public SpdyRstStreamFrame setStatus(SpdyStreamStatus var1) {
      this.status = var1;
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + StringUtil.NEWLINE + "--> Stream-ID = " + this.streamId() + StringUtil.NEWLINE + "--> Status: " + this.status();
   }
}
