package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class LoggingHandler extends ChannelDuplexHandler {
   private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
   protected final InternalLogger logger;
   protected final InternalLogLevel internalLevel;
   private final LogLevel level;
   private final ByteBufFormat byteBufFormat;

   public LoggingHandler() {
      this(DEFAULT_LEVEL);
   }

   public LoggingHandler(ByteBufFormat var1) {
      this(DEFAULT_LEVEL, var1);
   }

   public LoggingHandler(LogLevel var1) {
      this(var1, ByteBufFormat.HEX_DUMP);
   }

   public LoggingHandler(LogLevel var1, ByteBufFormat var2) {
      this.level = ObjectUtil.checkNotNull(var1, "level");
      this.byteBufFormat = ObjectUtil.checkNotNull(var2, "byteBufFormat");
      this.logger = InternalLoggerFactory.getInstance(this.getClass());
      this.internalLevel = var1.toInternalLevel();
   }

   public LoggingHandler(Class<?> var1) {
      this(var1, DEFAULT_LEVEL);
   }

   public LoggingHandler(Class<?> var1, LogLevel var2) {
      this(var1, var2, ByteBufFormat.HEX_DUMP);
   }

   public LoggingHandler(Class<?> var1, LogLevel var2, ByteBufFormat var3) {
      ObjectUtil.checkNotNull(var1, "clazz");
      this.level = ObjectUtil.checkNotNull(var2, "level");
      this.byteBufFormat = ObjectUtil.checkNotNull(var3, "byteBufFormat");
      this.logger = InternalLoggerFactory.getInstance(var1);
      this.internalLevel = var2.toInternalLevel();
   }

   public LoggingHandler(String var1) {
      this(var1, DEFAULT_LEVEL);
   }

   public LoggingHandler(String var1, LogLevel var2) {
      this(var1, var2, ByteBufFormat.HEX_DUMP);
   }

   public LoggingHandler(String var1, LogLevel var2, ByteBufFormat var3) {
      ObjectUtil.checkNotNull(var1, "name");
      this.level = ObjectUtil.checkNotNull(var2, "level");
      this.byteBufFormat = ObjectUtil.checkNotNull(var3, "byteBufFormat");
      this.logger = InternalLoggerFactory.getInstance(var1);
      this.internalLevel = var2.toInternalLevel();
   }

   public LogLevel level() {
      return this.level;
   }

   public ByteBufFormat byteBufFormat() {
      return this.byteBufFormat;
   }

   @Override
   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "REGISTERED"));
      }

      var1.fireChannelRegistered();
   }

   @Override
   public void channelUnregistered(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "UNREGISTERED"));
      }

      var1.fireChannelUnregistered();
   }

   @Override
   public void channelActive(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "ACTIVE"));
      }

      var1.fireChannelActive();
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "INACTIVE"));
      }

      var1.fireChannelInactive();
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "EXCEPTION", var2), var2);
      }

      var1.fireExceptionCaught(var2);
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "USER_EVENT", var2));
      }

      var1.fireUserEventTriggered(var2);
   }

   @Override
   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "BIND", var2));
      }

      var1.bind(var2, var3);
   }

   @Override
   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "CONNECT", var2, var3));
      }

      var1.connect(var2, var3, var4);
   }

   @Override
   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "DISCONNECT"));
      }

      var1.disconnect(var2);
   }

   @Override
   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "CLOSE"));
      }

      var1.close(var2);
   }

   @Override
   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "DEREGISTER"));
      }

      var1.deregister(var2);
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "READ COMPLETE"));
      }

      var1.fireChannelReadComplete();
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "READ", var2));
      }

      var1.fireChannelRead(var2);
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "WRITE", var2));
      }

      var1.write(var2, var3);
   }

   @Override
   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "WRITABILITY CHANGED"));
      }

      var1.fireChannelWritabilityChanged();
   }

   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.logger.isEnabled(this.internalLevel)) {
         this.logger.log(this.internalLevel, this.format(var1, "FLUSH"));
      }

      var1.flush();
   }

   protected String format(ChannelHandlerContext var1, String var2) {
      String var3 = var1.channel().toString();
      return new StringBuilder(var3.length() + 1 + var2.length()).append(var3).append(' ').append(var2).toString();
   }

   protected String format(ChannelHandlerContext var1, String var2, Object var3) {
      if (var3 instanceof ByteBuf) {
         return this.formatByteBuf(var1, var2, (ByteBuf)var3);
      } else {
         return var3 instanceof ByteBufHolder ? this.formatByteBufHolder(var1, var2, (ByteBufHolder)var3) : formatSimple(var1, var2, var3);
      }
   }

   protected String format(ChannelHandlerContext var1, String var2, Object var3, Object var4) {
      if (var4 == null) {
         return formatSimple(var1, var2, var3);
      } else {
         String var5 = var1.channel().toString();
         String var6 = String.valueOf(var3);
         String var7 = var4.toString();
         StringBuilder var8 = new StringBuilder(var5.length() + 1 + var2.length() + 2 + var6.length() + 2 + var7.length());
         var8.append(var5).append(' ').append(var2).append(": ").append(var6).append(", ").append(var7);
         return var8.toString();
      }
   }

   private String formatByteBuf(ChannelHandlerContext var1, String var2, ByteBuf var3) {
      String var4 = var1.channel().toString();
      int var5 = var3.readableBytes();
      if (var5 == 0) {
         StringBuilder var9 = new StringBuilder(var4.length() + 1 + var2.length() + 4);
         var9.append(var4).append(' ').append(var2).append(": 0B");
         return var9.toString();
      } else {
         int var6 = var4.length() + 1 + var2.length() + 2 + 10 + 1;
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            int var7 = var5 / 16 + (var5 % 15 == 0 ? 0 : 1) + 4;
            int var8 = 2 + var7 * 80;
            var6 += var8;
         }

         StringBuilder var10 = new StringBuilder(var6);
         var10.append(var4).append(' ').append(var2).append(": ").append(var5).append('B');
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            var10.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(var10, var3);
         }

         return var10.toString();
      }
   }

   private String formatByteBufHolder(ChannelHandlerContext var1, String var2, ByteBufHolder var3) {
      String var4 = var1.channel().toString();
      String var5 = var3.toString();
      ByteBuf var6 = var3.content();
      int var7 = var6.readableBytes();
      if (var7 == 0) {
         StringBuilder var11 = new StringBuilder(var4.length() + 1 + var2.length() + 2 + var5.length() + 4);
         var11.append(var4).append(' ').append(var2).append(", ").append(var5).append(", 0B");
         return var11.toString();
      } else {
         int var8 = var4.length() + 1 + var2.length() + 2 + var5.length() + 2 + 10 + 1;
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            int var9 = var7 / 16 + (var7 % 15 == 0 ? 0 : 1) + 4;
            int var10 = 2 + var9 * 80;
            var8 += var10;
         }

         StringBuilder var12 = new StringBuilder(var8);
         var12.append(var4).append(' ').append(var2).append(": ").append(var5).append(", ").append(var7).append('B');
         if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            var12.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(var12, var6);
         }

         return var12.toString();
      }
   }

   private static String formatSimple(ChannelHandlerContext var0, String var1, Object var2) {
      String var3 = var0.channel().toString();
      String var4 = String.valueOf(var2);
      StringBuilder var5 = new StringBuilder(var3.length() + 1 + var1.length() + 2 + var4.length());
      return var5.append(var3).append(' ').append(var1).append(": ").append(var4).toString();
   }
}
