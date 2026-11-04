package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public abstract class HttpObjectEncoder<H extends HttpMessage> extends MessageToMessageEncoder<Object> {
   static final int CRLF_SHORT = 3338;
   private static final int ZERO_CRLF_MEDIUM = 3149066;
   private static final byte[] ZERO_CRLF_CRLF = new byte[]{48, 13, 10, 13, 10};
   private static final ByteBuf CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(2).writeByte(13).writeByte(10)).asReadOnly();
   private static final ByteBuf ZERO_CRLF_CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(ZERO_CRLF_CRLF.length).writeBytes(ZERO_CRLF_CRLF))
      .asReadOnly();
   private static final float HEADERS_WEIGHT_NEW = 0.2F;
   private static final float HEADERS_WEIGHT_HISTORICAL = 0.8F;
   private static final float TRAILERS_WEIGHT_NEW = 0.2F;
   private static final float TRAILERS_WEIGHT_HISTORICAL = 0.8F;
   private static final int ST_INIT = 0;
   private static final int ST_CONTENT_NON_CHUNK = 1;
   private static final int ST_CONTENT_CHUNK = 2;
   private static final int ST_CONTENT_ALWAYS_EMPTY = 3;
   private int state = 0;
   private float headersEncodedSizeAccumulator = 256.0F;
   private float trailersEncodedSizeAccumulator = 256.0F;
   private final List<Object> out = new ArrayList<>();

   private static boolean checkContentState(int var0) {
      return var0 == 2 || var0 == 1 || var0 == 3;
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      try {
         if (this.acceptOutboundMessage(var2)) {
            this.encode(var1, var2, this.out);
            if (this.out.isEmpty()) {
               throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
            }
         } else {
            var1.write(var2, var3);
         }
      } catch (EncoderException var9) {
         throw var9;
      } catch (Throwable var10) {
         throw new EncoderException(var10);
      } finally {
         writeOutList(var1, this.out, var3);
      }
   }

   private static void writeOutList(ChannelHandlerContext var0, List<Object> var1, ChannelPromise var2) {
      int var3 = var1.size();

      try {
         if (var3 == 1) {
            var0.write(var1.get(0), var2);
         } else if (var3 > 1) {
            if (var2 == var0.voidPromise()) {
               writeVoidPromise(var0, var1);
            } else {
               writePromiseCombiner(var0, var1, var2);
            }
         }
      } finally {
         var1.clear();
      }
   }

   private static void writeVoidPromise(ChannelHandlerContext var0, List<Object> var1) {
      ChannelPromise var2 = var0.voidPromise();

      for (int var3 = 0; var3 < var1.size(); var3++) {
         var0.write(var1.get(var3), var2);
      }
   }

   private static void writePromiseCombiner(ChannelHandlerContext var0, List<Object> var1, ChannelPromise var2) {
      PromiseCombiner var3 = new PromiseCombiner(var0.executor());

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3.add(var0.write(var1.get(var4)));
      }

      var3.finish(var2);
   }

   @Override
   protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
      if (var2 == Unpooled.EMPTY_BUFFER) {
         var3.add(Unpooled.EMPTY_BUFFER);
      } else if (var2 instanceof FullHttpMessage) {
         this.encodeFullHttpMessage(var1, var2, var3);
      } else {
         if (var2 instanceof HttpMessage) {
            HttpMessage var4;
            try {
               var4 = (HttpMessage)var2;
            } catch (Exception var6) {
               ReferenceCountUtil.release(var2);
               throw var6;
            }

            if (var4 instanceof LastHttpContent) {
               this.encodeHttpMessageLastContent(var1, (H)var4, var3);
            } else if (var4 instanceof HttpContent) {
               this.encodeHttpMessageNotLastContent(var1, (H)var4, var3);
            }

            this.encodeJustHttpMessage(var1, (H)var4, var3);
         } else {
            this.encodeNotHttpMessageContentTypes(var1, var2, var3);
         }
      }
   }

   private void encodeJustHttpMessage(ChannelHandlerContext var1, H var2, List<Object> var3) throws Exception {
      assert !(var2 instanceof HttpContent);

      try {
         if (this.state != 0) {
            throwUnexpectedMessageTypeEx(var2, this.state);
         }

         ByteBuf var4 = this.encodeInitHttpMessage(var1, (H)var2);

         assert checkContentState(this.state);

         var3.add(var4);
      } finally {
         ReferenceCountUtil.release(var2);
      }
   }

   private void encodeByteBufHttpContent(int var1, ChannelHandlerContext var2, ByteBuf var3, ByteBuf var4, HttpHeaders var5, List<Object> var6) {
      switch (var1) {
         case 1:
            if (encodeContentNonChunk(var6, var3, var4)) {
               break;
            }
         case 3:
            var6.add(var3);
            break;
         case 2:
            var6.add(var3);
            this.encodeChunkedHttpContent(var2, var4, var5, var6);
            break;
         default:
            throw new Error();
      }
   }

   private void encodeHttpMessageNotLastContent(ChannelHandlerContext var1, H var2, List<Object> var3) throws Exception {
      assert var2 instanceof HttpContent;

      assert !(var2 instanceof LastHttpContent);

      HttpContent var4 = (HttpContent)var2;

      try {
         if (this.state != 0) {
            throwUnexpectedMessageTypeEx(var2, this.state);
         }

         ByteBuf var5 = this.encodeInitHttpMessage(var1, (H)var2);

         assert checkContentState(this.state);

         this.encodeByteBufHttpContent(this.state, var1, var5, var4.content(), null, var3);
      } finally {
         var4.release();
      }
   }

   private void encodeHttpMessageLastContent(ChannelHandlerContext var1, H var2, List<Object> var3) throws Exception {
      assert var2 instanceof LastHttpContent;

      LastHttpContent var4 = (LastHttpContent)var2;

      try {
         if (this.state != 0) {
            throwUnexpectedMessageTypeEx(var2, this.state);
         }

         ByteBuf var5 = this.encodeInitHttpMessage(var1, (H)var2);

         assert checkContentState(this.state);

         this.encodeByteBufHttpContent(this.state, var1, var5, var4.content(), var4.trailingHeaders(), var3);
         this.state = 0;
      } finally {
         var4.release();
      }
   }

   private void encodeNotHttpMessageContentTypes(ChannelHandlerContext var1, Object var2, List<Object> var3) {
      assert !(var2 instanceof HttpMessage);

      if (this.state == 0) {
         try {
            if (var2 instanceof ByteBuf && bypassEncoderIfEmpty((ByteBuf)var2, var3)) {
               return;
            }

            throwUnexpectedMessageTypeEx(var2, 0);
         } finally {
            ReferenceCountUtil.release(var2);
         }
      }

      if (var2 == LastHttpContent.EMPTY_LAST_CONTENT) {
         this.state = encodeEmptyLastHttpContent(this.state, var3);
      } else if (var2 instanceof LastHttpContent) {
         this.encodeLastHttpContent(var1, (LastHttpContent)var2, var3);
      } else if (var2 instanceof HttpContent) {
         this.encodeHttpContent(var1, (HttpContent)var2, var3);
      } else if (var2 instanceof ByteBuf) {
         this.encodeByteBufContent(var1, (ByteBuf)var2, var3);
      } else if (var2 instanceof FileRegion) {
         this.encodeFileRegionContent(var1, (FileRegion)var2, var3);
      } else {
         try {
            throwUnexpectedMessageTypeEx(var2, this.state);
         } finally {
            ReferenceCountUtil.release(var2);
         }
      }
   }

   private void encodeFullHttpMessage(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
      assert var2 instanceof FullHttpMessage;

      FullHttpMessage var4 = (FullHttpMessage)var2;

      try {
         if (this.state != 0) {
            throwUnexpectedMessageTypeEx(var2, this.state);
         }

         HttpMessage var5 = (HttpMessage)var2;
         ByteBuf var6 = var1.alloc().buffer((int)this.headersEncodedSizeAccumulator);
         this.encodeInitialLine(var6, (H)var5);
         int var7 = this.isContentAlwaysEmpty((H)var5) ? 3 : (HttpUtil.isTransferEncodingChunked(var5) ? 2 : 1);
         this.sanitizeHeadersBeforeEncode((H)var5, var7 == 3);
         this.encodeHeaders(var5.headers(), var6);
         ByteBufUtil.writeShortBE(var6, 3338);
         this.headersEncodedSizeAccumulator = 0.2F * (float)padSizeForAccumulation(var6.readableBytes()) + 0.8F * this.headersEncodedSizeAccumulator;
         this.encodeByteBufHttpContent(var7, var1, var6, var4.content(), var4.trailingHeaders(), var3);
      } finally {
         var4.release();
      }
   }

   private static boolean encodeContentNonChunk(List<Object> var0, ByteBuf var1, ByteBuf var2) {
      int var3 = var2.readableBytes();
      if (var3 > 0) {
         if (var1.writableBytes() >= var3) {
            var1.writeBytes(var2);
            var0.add(var1);
         } else {
            var0.add(var1);
            var0.add(var2.retain());
         }

         return true;
      } else {
         return false;
      }
   }

   private static void throwUnexpectedMessageTypeEx(Object var0, int var1) {
      throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(var0) + ", state: " + var1);
   }

   private void encodeFileRegionContent(ChannelHandlerContext var1, FileRegion var2, List<Object> var3) {
      try {
         assert this.state != 0;

         switch (this.state) {
            case 1:
               if (var2.count() > 0L) {
                  var3.add(var2.retain());
                  break;
               }
            case 3:
               var3.add(Unpooled.EMPTY_BUFFER);
               break;
            case 2:
               encodedChunkedFileRegionContent(var1, var2, var3);
               break;
            default:
               throw new Error();
         }
      } finally {
         var2.release();
      }
   }

   private static boolean bypassEncoderIfEmpty(ByteBuf var0, List<Object> var1) {
      if (!var0.isReadable()) {
         var1.add(var0.retain());
         return true;
      } else {
         return false;
      }
   }

   private void encodeByteBufContent(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      try {
         assert this.state != 0;

         if (!bypassEncoderIfEmpty(var2, var3)) {
            this.encodeByteBufAndTrailers(this.state, var1, var3, var2, null);
            return;
         }
      } finally {
         var2.release();
      }
   }

   private static int encodeEmptyLastHttpContent(int var0, List<Object> var1) {
      assert var0 != 0;

      switch (var0) {
         case 1:
         case 3:
            var1.add(Unpooled.EMPTY_BUFFER);
            break;
         case 2:
            var1.add(ZERO_CRLF_CRLF_BUF.duplicate());
            break;
         default:
            throw new Error();
      }

      return 0;
   }

   private void encodeLastHttpContent(ChannelHandlerContext var1, LastHttpContent var2, List<Object> var3) {
      assert this.state != 0;

      assert !(var2 instanceof HttpMessage);

      try {
         this.encodeByteBufAndTrailers(this.state, var1, var3, var2.content(), var2.trailingHeaders());
         this.state = 0;
      } finally {
         var2.release();
      }
   }

   private void encodeHttpContent(ChannelHandlerContext var1, HttpContent var2, List<Object> var3) {
      assert this.state != 0;

      assert !(var2 instanceof HttpMessage);

      assert !(var2 instanceof LastHttpContent);

      try {
         this.encodeByteBufAndTrailers(this.state, var1, var3, var2.content(), null);
      } finally {
         var2.release();
      }
   }

   private void encodeByteBufAndTrailers(int var1, ChannelHandlerContext var2, List<Object> var3, ByteBuf var4, HttpHeaders var5) {
      switch (var1) {
         case 1:
            if (var4.isReadable()) {
               var3.add(var4.retain());
               break;
            }
         case 3:
            var3.add(Unpooled.EMPTY_BUFFER);
            break;
         case 2:
            this.encodeChunkedHttpContent(var2, var4, var5, var3);
            break;
         default:
            throw new Error();
      }
   }

   private void encodeChunkedHttpContent(ChannelHandlerContext var1, ByteBuf var2, HttpHeaders var3, List<Object> var4) {
      int var5 = var2.readableBytes();
      if (var5 > 0) {
         addEncodedLengthHex(var1, (long)var5, var4);
         var4.add(var2.retain());
         var4.add(CRLF_BUF.duplicate());
      }

      if (var3 != null) {
         this.encodeTrailingHeaders(var1, var3, var4);
      } else if (var5 == 0) {
         var4.add(var2.retain());
      }
   }

   private void encodeTrailingHeaders(ChannelHandlerContext var1, HttpHeaders var2, List<Object> var3) {
      if (var2.isEmpty()) {
         var3.add(ZERO_CRLF_CRLF_BUF.duplicate());
      } else {
         ByteBuf var4 = var1.alloc().buffer((int)this.trailersEncodedSizeAccumulator);
         ByteBufUtil.writeMediumBE(var4, 3149066);
         this.encodeHeaders(var2, var4);
         ByteBufUtil.writeShortBE(var4, 3338);
         this.trailersEncodedSizeAccumulator = 0.2F * (float)padSizeForAccumulation(var4.readableBytes()) + 0.8F * this.trailersEncodedSizeAccumulator;
         var3.add(var4);
      }
   }

   private ByteBuf encodeInitHttpMessage(ChannelHandlerContext var1, H var2) throws Exception {
      assert this.state == 0;

      ByteBuf var3 = var1.alloc().buffer((int)this.headersEncodedSizeAccumulator);
      this.encodeInitialLine(var3, (H)var2);
      this.state = this.isContentAlwaysEmpty((H)var2) ? 3 : (HttpUtil.isTransferEncodingChunked(var2) ? 2 : 1);
      this.sanitizeHeadersBeforeEncode((H)var2, this.state == 3);
      this.encodeHeaders(var2.headers(), var3);
      ByteBufUtil.writeShortBE(var3, 3338);
      this.headersEncodedSizeAccumulator = 0.2F * (float)padSizeForAccumulation(var3.readableBytes()) + 0.8F * this.headersEncodedSizeAccumulator;
      return var3;
   }

   protected void encodeHeaders(HttpHeaders var1, ByteBuf var2) {
      Iterator var3 = var1.iteratorCharSequence();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         HttpHeadersEncoder.encoderHeader((CharSequence)var4.getKey(), (CharSequence)var4.getValue(), var2);
      }
   }

   private static void encodedChunkedFileRegionContent(ChannelHandlerContext var0, FileRegion var1, List<Object> var2) {
      long var3 = var1.count();
      if (var3 > 0L) {
         addEncodedLengthHex(var0, var3, var2);
         var2.add(var1.retain());
         var2.add(CRLF_BUF.duplicate());
      } else if (var3 == 0L) {
         var2.add(var1.retain());
      }
   }

   private static void addEncodedLengthHex(ChannelHandlerContext var0, long var1, List<Object> var3) {
      String var4 = Long.toHexString(var1);
      ByteBuf var5 = var0.alloc().buffer(var4.length() + 2);
      var5.writeCharSequence(var4, CharsetUtil.US_ASCII);
      ByteBufUtil.writeShortBE(var5, 3338);
      var3.add(var5);
   }

   protected void sanitizeHeadersBeforeEncode(H var1, boolean var2) {
   }

   protected boolean isContentAlwaysEmpty(H var1) {
      return false;
   }

   @Override
   public boolean acceptOutboundMessage(Object var1) throws Exception {
      return var1 == Unpooled.EMPTY_BUFFER
         || var1 == LastHttpContent.EMPTY_LAST_CONTENT
         || var1 instanceof FullHttpMessage
         || var1 instanceof HttpMessage
         || var1 instanceof LastHttpContent
         || var1 instanceof HttpContent
         || var1 instanceof ByteBuf
         || var1 instanceof FileRegion;
   }

   private static int padSizeForAccumulation(int var0) {
      return (var0 << 2) / 3;
   }

   @Deprecated
   protected static void encodeAscii(String var0, ByteBuf var1) {
      var1.writeCharSequence(var0, CharsetUtil.US_ASCII);
   }

   protected abstract void encodeInitialLine(ByteBuf var1, H var2) throws Exception;
}
