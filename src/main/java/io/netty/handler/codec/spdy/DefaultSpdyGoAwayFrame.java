package io.netty.handler.codec.spdy;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyGoAwayFrame implements SpdyGoAwayFrame {
   private int lastGoodStreamId;
   private SpdySessionStatus status;

   public DefaultSpdyGoAwayFrame(int var1) {
      this(var1, 0);
   }

   public DefaultSpdyGoAwayFrame(int var1, int var2) {
      this(var1, SpdySessionStatus.valueOf(var2));
   }

   public DefaultSpdyGoAwayFrame(int var1, SpdySessionStatus var2) {
      this.setLastGoodStreamId(var1);
      this.setStatus(var2);
   }

   @Override
   public int lastGoodStreamId() {
      return this.lastGoodStreamId;
   }

   @Override
   public SpdyGoAwayFrame setLastGoodStreamId(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "lastGoodStreamId");
      this.lastGoodStreamId = var1;
      return this;
   }

   @Override
   public SpdySessionStatus status() {
      return this.status;
   }

   @Override
   public SpdyGoAwayFrame setStatus(SpdySessionStatus var1) {
      this.status = var1;
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + StringUtil.NEWLINE
         + "--> Last-good-stream-ID = "
         + this.lastGoodStreamId()
         + StringUtil.NEWLINE
         + "--> Status: "
         + this.status();
   }
}
