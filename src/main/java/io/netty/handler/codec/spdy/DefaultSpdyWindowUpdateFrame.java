package io.netty.handler.codec.spdy;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyWindowUpdateFrame implements SpdyWindowUpdateFrame {
   private int streamId;
   private int deltaWindowSize;

   public DefaultSpdyWindowUpdateFrame(int var1, int var2) {
      this.setStreamId(var1);
      this.setDeltaWindowSize(var2);
   }

   @Override
   public int streamId() {
      return this.streamId;
   }

   @Override
   public SpdyWindowUpdateFrame setStreamId(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "streamId");
      this.streamId = var1;
      return this;
   }

   @Override
   public int deltaWindowSize() {
      return this.deltaWindowSize;
   }

   @Override
   public SpdyWindowUpdateFrame setDeltaWindowSize(int var1) {
      ObjectUtil.checkPositive(var1, "deltaWindowSize");
      this.deltaWindowSize = var1;
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + StringUtil.NEWLINE
         + "--> Stream-ID = "
         + this.streamId()
         + StringUtil.NEWLINE
         + "--> Delta-Window-Size = "
         + this.deltaWindowSize();
   }
}
