package io.netty.handler.codec.spdy;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame extends DefaultSpdyHeadersFrame implements SpdySynStreamFrame {
   private int associatedStreamId;
   private byte priority;
   private boolean unidirectional;

   public DefaultSpdySynStreamFrame(int var1, int var2, byte var3) {
      this(var1, var2, var3, true);
   }

   public DefaultSpdySynStreamFrame(int var1, int var2, byte var3, boolean var4) {
      super(var1, var4);
      this.setAssociatedStreamId(var2);
      this.setPriority(var3);
   }

   @Override
   public SpdySynStreamFrame setStreamId(int var1) {
      super.setStreamId(var1);
      return this;
   }

   @Override
   public SpdySynStreamFrame setLast(boolean var1) {
      super.setLast(var1);
      return this;
   }

   @Override
   public SpdySynStreamFrame setInvalid() {
      super.setInvalid();
      return this;
   }

   @Override
   public int associatedStreamId() {
      return this.associatedStreamId;
   }

   @Override
   public SpdySynStreamFrame setAssociatedStreamId(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "associatedStreamId");
      this.associatedStreamId = var1;
      return this;
   }

   @Override
   public byte priority() {
      return this.priority;
   }

   @Override
   public SpdySynStreamFrame setPriority(byte var1) {
      if (var1 >= 0 && var1 <= 7) {
         this.priority = var1;
         return this;
      } else {
         throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + var1);
      }
   }

   @Override
   public boolean isUnidirectional() {
      return this.unidirectional;
   }

   @Override
   public SpdySynStreamFrame setUnidirectional(boolean var1) {
      this.unidirectional = var1;
      return this;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder()
         .append(StringUtil.simpleClassName(this))
         .append("(last: ")
         .append(this.isLast())
         .append("; unidirectional: ")
         .append(this.isUnidirectional())
         .append(')')
         .append(StringUtil.NEWLINE)
         .append("--> Stream-ID = ")
         .append(this.streamId())
         .append(StringUtil.NEWLINE);
      if (this.associatedStreamId != 0) {
         var1.append("--> Associated-To-Stream-ID = ").append(this.associatedStreamId()).append(StringUtil.NEWLINE);
      }

      var1.append("--> Priority = ").append(this.priority()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
      this.appendHeaders(var1);
      var1.setLength(var1.length() - StringUtil.NEWLINE.length());
      return var1.toString();
   }
}
