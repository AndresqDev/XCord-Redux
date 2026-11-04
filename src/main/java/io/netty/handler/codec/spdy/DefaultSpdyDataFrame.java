package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyDataFrame extends DefaultSpdyStreamFrame implements SpdyDataFrame {
   private final ByteBuf data;

   public DefaultSpdyDataFrame(int var1) {
      this(var1, Unpooled.buffer(0));
   }

   public DefaultSpdyDataFrame(int var1, ByteBuf var2) {
      super(var1);
      this.data = validate(ObjectUtil.checkNotNull(var2, "data"));
   }

   private static ByteBuf validate(ByteBuf var0) {
      if (var0.readableBytes() > 16777215) {
         throw new IllegalArgumentException("data payload cannot exceed 16777215 bytes");
      } else {
         return var0;
      }
   }

   @Override
   public SpdyDataFrame setStreamId(int var1) {
      super.setStreamId(var1);
      return this;
   }

   @Override
   public SpdyDataFrame setLast(boolean var1) {
      super.setLast(var1);
      return this;
   }

   @Override
   public ByteBuf content() {
      return ByteBufUtil.ensureAccessible(this.data);
   }

   @Override
   public SpdyDataFrame copy() {
      return this.replace(this.content().copy());
   }

   @Override
   public SpdyDataFrame duplicate() {
      return this.replace(this.content().duplicate());
   }

   @Override
   public SpdyDataFrame retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   @Override
   public SpdyDataFrame replace(ByteBuf var1) {
      DefaultSpdyDataFrame var2 = new DefaultSpdyDataFrame(this.streamId(), var1);
      var2.setLast(this.isLast());
      return var2;
   }

   @Override
   public int refCnt() {
      return this.data.refCnt();
   }

   @Override
   public SpdyDataFrame retain() {
      this.data.retain();
      return this;
   }

   @Override
   public SpdyDataFrame retain(int var1) {
      this.data.retain(var1);
      return this;
   }

   @Override
   public SpdyDataFrame touch() {
      this.data.touch();
      return this;
   }

   @Override
   public SpdyDataFrame touch(Object var1) {
      this.data.touch(var1);
      return this;
   }

   @Override
   public boolean release() {
      return this.data.release();
   }

   @Override
   public boolean release(int var1) {
      return this.data.release(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder()
         .append(StringUtil.simpleClassName(this))
         .append("(last: ")
         .append(this.isLast())
         .append(')')
         .append(StringUtil.NEWLINE)
         .append("--> Stream-ID = ")
         .append(this.streamId())
         .append(StringUtil.NEWLINE)
         .append("--> Size = ");
      if (this.refCnt() == 0) {
         var1.append("(freed)");
      } else {
         var1.append(this.content().readableBytes());
      }

      return var1.toString();
   }
}
