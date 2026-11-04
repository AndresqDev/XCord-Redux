package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpdyHttpDecoder extends MessageToMessageDecoder<SpdyFrame> {
   private final boolean validateHeaders;
   private final int spdyVersion;
   private final int maxContentLength;
   private final Map<Integer, FullHttpMessage> messageMap;

   public SpdyHttpDecoder(SpdyVersion var1, int var2) {
      this(var1, var2, new HashMap<>(), true);
   }

   public SpdyHttpDecoder(SpdyVersion var1, int var2, boolean var3) {
      this(var1, var2, new HashMap<>(), var3);
   }

   protected SpdyHttpDecoder(SpdyVersion var1, int var2, Map<Integer, FullHttpMessage> var3) {
      this(var1, var2, var3, true);
   }

   protected SpdyHttpDecoder(SpdyVersion var1, int var2, Map<Integer, FullHttpMessage> var3, boolean var4) {
      this.spdyVersion = ObjectUtil.checkNotNull(var1, "version").getVersion();
      this.maxContentLength = ObjectUtil.checkPositive(var2, "maxContentLength");
      this.messageMap = var3;
      this.validateHeaders = var4;
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      for (Entry var3 : this.messageMap.entrySet()) {
         ReferenceCountUtil.safeRelease(var3.getValue());
      }

      this.messageMap.clear();
      super.channelInactive(var1);
   }

   protected FullHttpMessage putMessage(int var1, FullHttpMessage var2) {
      return this.messageMap.put(var1, var2);
   }

   protected FullHttpMessage getMessage(int var1) {
      return this.messageMap.get(var1);
   }

   protected FullHttpMessage removeMessage(int var1) {
      return this.messageMap.remove(var1);
   }

   protected void decode(ChannelHandlerContext var1, SpdyFrame var2, List<Object> var3) throws Exception {
      if (var2 instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame var4 = (SpdySynStreamFrame)var2;
         int var5 = var4.streamId();
         if (SpdyCodecUtil.isServerId(var5)) {
            int var6 = var4.associatedStreamId();
            if (var6 == 0) {
               DefaultSpdyRstStreamFrame var31 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.INVALID_STREAM);
               var1.writeAndFlush(var31);
               return;
            }

            if (var4.isLast()) {
               DefaultSpdyRstStreamFrame var30 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.PROTOCOL_ERROR);
               var1.writeAndFlush(var30);
               return;
            }

            if (var4.isTruncated()) {
               DefaultSpdyRstStreamFrame var29 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.INTERNAL_ERROR);
               var1.writeAndFlush(var29);
               return;
            }

            try {
               FullHttpRequest var7 = createHttpRequest(var4, var1.alloc());
               var7.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var5);
               var7.headers().setInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, var6);
               var7.headers().setInt(SpdyHttpHeaders.Names.PRIORITY, var4.priority());
               var3.add(var7);
            } catch (Throwable var13) {
               DefaultSpdyRstStreamFrame var8 = new DefaultSpdyRstStreamFrame(var5, SpdyStreamStatus.PROTOCOL_ERROR);
               var1.writeAndFlush(var8);
            }
         } else {
            if (var4.isTruncated()) {
               DefaultSpdySynReplyFrame var23 = new DefaultSpdySynReplyFrame(var5);
               var23.setLast(true);
               SpdyHeaders var33 = var23.headers();
               var33.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.code());
               var33.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
               var1.writeAndFlush(var23);
               return;
            }

            try {
               FullHttpRequest var22 = createHttpRequest(var4, var1.alloc());
               var22.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var5);
               if (var4.isLast()) {
                  var3.add(var22);
               } else {
                  this.putMessage(var5, var22);
               }
            } catch (Throwable var12) {
               DefaultSpdySynReplyFrame var32 = new DefaultSpdySynReplyFrame(var5);
               var32.setLast(true);
               SpdyHeaders var38 = var32.headers();
               var38.setInt(SpdyHeaders.HttpNames.STATUS, HttpResponseStatus.BAD_REQUEST.code());
               var38.setObject(SpdyHeaders.HttpNames.VERSION, HttpVersion.HTTP_1_0);
               var1.writeAndFlush(var32);
            }
         }
      } else if (var2 instanceof SpdySynReplyFrame) {
         SpdySynReplyFrame var14 = (SpdySynReplyFrame)var2;
         int var18 = var14.streamId();
         if (var14.isTruncated()) {
            DefaultSpdyRstStreamFrame var25 = new DefaultSpdyRstStreamFrame(var18, SpdyStreamStatus.INTERNAL_ERROR);
            var1.writeAndFlush(var25);
            return;
         }

         try {
            FullHttpResponse var24 = createHttpResponse(var14, var1.alloc(), this.validateHeaders);
            var24.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var18);
            if (var14.isLast()) {
               HttpUtil.setContentLength(var24, 0L);
               var3.add(var24);
            } else {
               this.putMessage(var18, var24);
            }
         } catch (Throwable var11) {
            DefaultSpdyRstStreamFrame var34 = new DefaultSpdyRstStreamFrame(var18, SpdyStreamStatus.PROTOCOL_ERROR);
            var1.writeAndFlush(var34);
         }
      } else if (var2 instanceof SpdyHeadersFrame) {
         SpdyHeadersFrame var15 = (SpdyHeadersFrame)var2;
         int var19 = var15.streamId();
         FullHttpMessage var26 = this.getMessage(var19);
         if (var26 == null) {
            if (SpdyCodecUtil.isServerId(var19)) {
               if (var15.isTruncated()) {
                  DefaultSpdyRstStreamFrame var36 = new DefaultSpdyRstStreamFrame(var19, SpdyStreamStatus.INTERNAL_ERROR);
                  var1.writeAndFlush(var36);
                  return;
               }

               try {
                  FullHttpResponse var27 = createHttpResponse(var15, var1.alloc(), this.validateHeaders);
                  var27.headers().setInt(SpdyHttpHeaders.Names.STREAM_ID, var19);
                  if (var15.isLast()) {
                     HttpUtil.setContentLength(var27, 0L);
                     var3.add(var27);
                  } else {
                     this.putMessage(var19, var27);
                  }
               } catch (Throwable var10) {
                  DefaultSpdyRstStreamFrame var40 = new DefaultSpdyRstStreamFrame(var19, SpdyStreamStatus.PROTOCOL_ERROR);
                  var1.writeAndFlush(var40);
               }
            }

            return;
         }

         if (!var15.isTruncated()) {
            for (Entry var39 : var15.headers()) {
               var26.headers().add((CharSequence)var39.getKey(), var39.getValue());
            }
         }

         if (var15.isLast()) {
            HttpUtil.setContentLength(var26, (long)var26.content().readableBytes());
            this.removeMessage(var19);
            var3.add(var26);
         }
      } else if (var2 instanceof SpdyDataFrame) {
         SpdyDataFrame var16 = (SpdyDataFrame)var2;
         int var20 = var16.streamId();
         FullHttpMessage var28 = this.getMessage(var20);
         if (var28 == null) {
            return;
         }

         ByteBuf var37 = var28.content();
         if (var37.readableBytes() > this.maxContentLength - var16.content().readableBytes()) {
            this.removeMessage(var20);
            throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
         }

         ByteBuf var41 = var16.content();
         int var9 = var41.readableBytes();
         var37.writeBytes(var41, var41.readerIndex(), var9);
         if (var16.isLast()) {
            HttpUtil.setContentLength(var28, (long)var37.readableBytes());
            this.removeMessage(var20);
            var3.add(var28);
         }
      } else if (var2 instanceof SpdyRstStreamFrame) {
         SpdyRstStreamFrame var17 = (SpdyRstStreamFrame)var2;
         int var21 = var17.streamId();
         this.removeMessage(var21);
      }
   }

   private static FullHttpRequest createHttpRequest(SpdyHeadersFrame var0, ByteBufAllocator var1) throws Exception {
      SpdyHeaders var2 = var0.headers();
      HttpMethod var3 = HttpMethod.valueOf(var2.getAsString(SpdyHeaders.HttpNames.METHOD));
      String var4 = var2.getAsString(SpdyHeaders.HttpNames.PATH);
      HttpVersion var5 = HttpVersion.valueOf(var2.getAsString(SpdyHeaders.HttpNames.VERSION));
      var2.remove(SpdyHeaders.HttpNames.METHOD);
      var2.remove(SpdyHeaders.HttpNames.PATH);
      var2.remove(SpdyHeaders.HttpNames.VERSION);
      boolean var6 = true;
      ByteBuf var7 = var1.buffer();

      DefaultFullHttpRequest var15;
      try {
         DefaultFullHttpRequest var8 = new DefaultFullHttpRequest(var5, var3, var4, var7);
         var2.remove(SpdyHeaders.HttpNames.SCHEME);
         CharSequence var9 = var2.get(SpdyHeaders.HttpNames.HOST);
         var2.remove(SpdyHeaders.HttpNames.HOST);
         var8.headers().set(HttpHeaderNames.HOST, var9);

         for (Entry var11 : var0.headers()) {
            var8.headers().add((CharSequence)var11.getKey(), var11.getValue());
         }

         HttpUtil.setKeepAlive(var8, true);
         var8.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
         var6 = false;
         var15 = var8;
      } finally {
         if (var6) {
            var7.release();
         }
      }

      return var15;
   }

   private static FullHttpResponse createHttpResponse(SpdyHeadersFrame var0, ByteBufAllocator var1, boolean var2) throws Exception {
      SpdyHeaders var3 = var0.headers();
      HttpResponseStatus var4 = HttpResponseStatus.parseLine(var3.get(SpdyHeaders.HttpNames.STATUS));
      HttpVersion var5 = HttpVersion.valueOf(var3.getAsString(SpdyHeaders.HttpNames.VERSION));
      var3.remove(SpdyHeaders.HttpNames.STATUS);
      var3.remove(SpdyHeaders.HttpNames.VERSION);
      boolean var6 = true;
      ByteBuf var7 = var1.buffer();

      DefaultFullHttpResponse var14;
      try {
         DefaultFullHttpResponse var8 = new DefaultFullHttpResponse(var5, var4, var7, var2);

         for (Entry var10 : var0.headers()) {
            var8.headers().add((CharSequence)var10.getKey(), var10.getValue());
         }

         HttpUtil.setKeepAlive(var8, true);
         var8.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
         var8.headers().remove(HttpHeaderNames.TRAILER);
         var6 = false;
         var14 = var8;
      } finally {
         if (var6) {
            var7.release();
         }
      }

      return var14;
   }
}
