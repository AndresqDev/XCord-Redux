package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public abstract class HttpObjectDecoder extends ByteToMessageDecoder {
   public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
   public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
   public static final boolean DEFAULT_CHUNKED_SUPPORTED = true;
   public static final boolean DEFAULT_ALLOW_PARTIAL_CHUNKS = true;
   public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
   public static final boolean DEFAULT_VALIDATE_HEADERS = true;
   public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
   public static final boolean DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS = false;
   private static final String EMPTY_VALUE = "";
   private final int maxChunkSize;
   private final boolean chunkedSupported;
   private final boolean allowPartialChunks;
   protected final boolean validateHeaders;
   private final boolean allowDuplicateContentLengths;
   private final HttpObjectDecoder.HeaderParser headerParser;
   private final HttpObjectDecoder.LineParser lineParser;
   private HttpMessage message;
   private long chunkSize;
   private long contentLength = Long.MIN_VALUE;
   private volatile boolean resetRequested;
   private CharSequence name;
   private CharSequence value;
   private LastHttpContent trailer;
   private HttpObjectDecoder.State currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;

   protected HttpObjectDecoder() {
      this(4096, 8192, 8192, true);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, true);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, 128);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4, boolean var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4, boolean var5, int var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, var7, true);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4, boolean var5, int var6, boolean var7, boolean var8) {
      ObjectUtil.checkPositive(var1, "maxInitialLineLength");
      ObjectUtil.checkPositive(var2, "maxHeaderSize");
      ObjectUtil.checkPositive(var3, "maxChunkSize");
      AppendableCharSequence var9 = new AppendableCharSequence(var6);
      this.lineParser = new HttpObjectDecoder.LineParser(var9, var1);
      this.headerParser = new HttpObjectDecoder.HeaderParser(var9, var2);
      this.maxChunkSize = var3;
      this.chunkedSupported = var4;
      this.validateHeaders = var5;
      this.allowDuplicateContentLengths = var7;
      this.allowPartialChunks = var8;
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.resetRequested) {
         this.resetNow();
      }

      switch (this.currentState) {
         case SKIP_CONTROL_CHARS:
         case READ_INITIAL:
            try {
               AppendableCharSequence var18 = this.lineParser.parse(var2);
               if (var18 == null) {
                  return;
               }

               String[] var24 = splitInitialLine(var18);
               if (var24.length < 3) {
                  this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
                  return;
               }

               this.message = this.createMessage(var24);
               this.currentState = HttpObjectDecoder.State.READ_HEADER;
            } catch (Exception var9) {
               var3.add(this.invalidMessage(var2, var9));
               return;
            }
         case READ_HEADER:
            try {
               HttpObjectDecoder.State var19 = this.readHeaders(var2);
               if (var19 == null) {
                  return;
               }

               this.currentState = var19;
               switch (var19) {
                  case SKIP_CONTROL_CHARS:
                     var3.add(this.message);
                     var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
                     this.resetNow();
                     return;
                  case READ_CHUNK_SIZE:
                     if (!this.chunkedSupported) {
                        throw new IllegalArgumentException("Chunked messages not supported");
                     }

                     var3.add(this.message);
                     return;
                  default:
                     long var25 = this.contentLength();
                     if (var25 != 0L && (var25 != -1L || !this.isDecodingRequest())) {
                        assert var19 == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT || var19 == HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;

                        var3.add(this.message);
                        if (var19 == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT) {
                           this.chunkSize = var25;
                        }

                        return;
                     }

                     var3.add(this.message);
                     var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
                     this.resetNow();
                     return;
               }
            } catch (Exception var10) {
               var3.add(this.invalidMessage(var2, var10));
               return;
            }
         case READ_CHUNK_SIZE:
            try {
               AppendableCharSequence var14 = this.lineParser.parse(var2);
               if (var14 == null) {
                  return;
               }

               int var21 = getChunkSize(var14.toString());
               this.chunkSize = (long)var21;
               if (var21 == 0) {
                  this.currentState = HttpObjectDecoder.State.READ_CHUNK_FOOTER;
                  return;
               }

               this.currentState = HttpObjectDecoder.State.READ_CHUNKED_CONTENT;
            } catch (Exception var8) {
               var3.add(this.invalidChunk(var2, var8));
               return;
            }
         case READ_CHUNKED_CONTENT:
            assert this.chunkSize <= 2147483647L;

            int var15 = Math.min((int)this.chunkSize, this.maxChunkSize);
            if (!this.allowPartialChunks && var2.readableBytes() < var15) {
               return;
            }

            var15 = Math.min(var15, var2.readableBytes());
            if (var15 == 0) {
               return;
            }

            DefaultHttpContent var22 = new DefaultHttpContent(var2.readRetainedSlice(var15));
            this.chunkSize -= (long)var15;
            var3.add(var22);
            if (this.chunkSize != 0L) {
               return;
            }

            this.currentState = HttpObjectDecoder.State.READ_CHUNK_DELIMITER;
         case READ_CHUNK_DELIMITER:
            int var17 = var2.writerIndex();
            int var23 = var2.readerIndex();

            while (var17 > var23) {
               byte var26 = var2.getByte(var23++);
               if (var26 == 10) {
                  this.currentState = HttpObjectDecoder.State.READ_CHUNK_SIZE;
                  break;
               }
            }

            var2.readerIndex(var23);
            return;
         case READ_VARIABLE_LENGTH_CONTENT:
            int var13 = Math.min(var2.readableBytes(), this.maxChunkSize);
            if (var13 > 0) {
               ByteBuf var20 = var2.readRetainedSlice(var13);
               var3.add(new DefaultHttpContent(var20));
            }

            return;
         case READ_FIXED_LENGTH_CONTENT:
            int var12 = var2.readableBytes();
            if (var12 == 0) {
               return;
            }

            int var5 = Math.min(var12, this.maxChunkSize);
            if ((long)var5 > this.chunkSize) {
               var5 = (int)this.chunkSize;
            }

            ByteBuf var6 = var2.readRetainedSlice(var5);
            this.chunkSize -= (long)var5;
            if (this.chunkSize == 0L) {
               var3.add(new DefaultLastHttpContent(var6, this.validateHeaders));
               this.resetNow();
            } else {
               var3.add(new DefaultHttpContent(var6));
            }

            return;
         case READ_CHUNK_FOOTER:
            try {
               LastHttpContent var11 = this.readTrailingHeaders(var2);
               if (var11 == null) {
                  return;
               }

               var3.add(var11);
               this.resetNow();
               return;
            } catch (Exception var7) {
               var3.add(this.invalidChunk(var2, var7));
               return;
            }
         case BAD_MESSAGE:
            var2.skipBytes(var2.readableBytes());
            break;
         case UPGRADED:
            int var4 = var2.readableBytes();
            if (var4 > 0) {
               var3.add(var2.readBytes(var4));
            }
      }
   }

   @Override
   protected void decodeLast(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      super.decodeLast(var1, var2, var3);
      if (this.resetRequested) {
         this.resetNow();
      }

      if (this.message != null) {
         boolean var4 = HttpUtil.isTransferEncodingChunked(this.message);
         if (this.currentState == HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT && !var2.isReadable() && !var4) {
            var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
            this.resetNow();
            return;
         }

         if (this.currentState == HttpObjectDecoder.State.READ_HEADER) {
            var3.add(this.invalidMessage(Unpooled.EMPTY_BUFFER, new PrematureChannelClosureException("Connection closed before received headers")));
            this.resetNow();
            return;
         }

         boolean var5;
         if (!this.isDecodingRequest() && !var4) {
            var5 = this.contentLength() > 0L;
         } else {
            var5 = true;
         }

         if (!var5) {
            var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
         }

         this.resetNow();
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpExpectationFailedEvent) {
         switch (this.currentState) {
            case READ_CHUNK_SIZE:
            case READ_VARIABLE_LENGTH_CONTENT:
            case READ_FIXED_LENGTH_CONTENT:
               this.reset();
            case READ_INITIAL:
            case READ_HEADER:
         }
      }

      super.userEventTriggered(var1, var2);
   }

   protected boolean isContentAlwaysEmpty(HttpMessage var1) {
      if (!(var1 instanceof HttpResponse)) {
         return false;
      } else {
         HttpResponse var2 = (HttpResponse)var1;
         int var3 = var2.status().code();
         if (var3 >= 100 && var3 < 200) {
            return var3 != 101
               || var2.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT)
               || !var2.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
         } else {
            switch (var3) {
               case 204:
               case 304:
                  return true;
               default:
                  return false;
            }
         }
      }
   }

   protected boolean isSwitchingToNonHttp1Protocol(HttpResponse var1) {
      if (var1.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
         return false;
      } else {
         String var2 = var1.headers().get(HttpHeaderNames.UPGRADE);
         return var2 == null || !var2.contains(HttpVersion.HTTP_1_0.text()) && !var2.contains(HttpVersion.HTTP_1_1.text());
      }
   }

   public void reset() {
      this.resetRequested = true;
   }

   private void resetNow() {
      HttpMessage var1 = this.message;
      this.message = null;
      this.name = null;
      this.value = null;
      this.contentLength = Long.MIN_VALUE;
      this.lineParser.reset();
      this.headerParser.reset();
      this.trailer = null;
      if (!this.isDecodingRequest()) {
         HttpResponse var2 = (HttpResponse)var1;
         if (var2 != null && this.isSwitchingToNonHttp1Protocol(var2)) {
            this.currentState = HttpObjectDecoder.State.UPGRADED;
            return;
         }
      }

      this.resetRequested = false;
      this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
   }

   private HttpMessage invalidMessage(ByteBuf var1, Exception var2) {
      this.currentState = HttpObjectDecoder.State.BAD_MESSAGE;
      var1.skipBytes(var1.readableBytes());
      if (this.message == null) {
         this.message = this.createInvalidMessage();
      }

      this.message.setDecoderResult(DecoderResult.failure(var2));
      HttpMessage var3 = this.message;
      this.message = null;
      return var3;
   }

   private HttpContent invalidChunk(ByteBuf var1, Exception var2) {
      this.currentState = HttpObjectDecoder.State.BAD_MESSAGE;
      var1.skipBytes(var1.readableBytes());
      DefaultLastHttpContent var3 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
      var3.setDecoderResult(DecoderResult.failure(var2));
      this.message = null;
      this.trailer = null;
      return var3;
   }

   private HttpObjectDecoder.State readHeaders(ByteBuf var1) {
      HttpMessage var2 = this.message;
      HttpHeaders var3 = var2.headers();
      AppendableCharSequence var4 = this.headerParser.parse(var1);
      if (var4 == null) {
         return null;
      } else {
         if (var4.length() > 0) {
            do {
               char var5 = var4.charAtUnsafe(0);
               if (this.name == null || var5 != ' ' && var5 != '\t') {
                  if (this.name != null) {
                     var3.add(this.name, this.value);
                  }

                  this.splitHeader(var4);
               } else {
                  String var6 = var4.toString().trim();
                  String var7 = String.valueOf(this.value);
                  this.value = var7 + ' ' + var6;
               }

               var4 = this.headerParser.parse(var1);
               if (var4 == null) {
                  return null;
               }
            } while (var4.length() > 0);
         }

         if (this.name != null) {
            var3.add(this.name, this.value);
         }

         this.name = null;
         this.value = null;
         HttpMessageDecoderResult var9 = new HttpMessageDecoderResult(this.lineParser.size, this.headerParser.size);
         var2.setDecoderResult(var9);
         List var10 = var3.getAll(HttpHeaderNames.CONTENT_LENGTH);
         if (!var10.isEmpty()) {
            HttpVersion var11 = var2.protocolVersion();
            boolean var8 = var11.majorVersion() < 1 || var11.majorVersion() == 1 && var11.minorVersion() == 0;
            this.contentLength = HttpUtil.normalizeAndGetContentLength(var10, var8, this.allowDuplicateContentLengths);
            if (this.contentLength != -1L) {
               var3.set(HttpHeaderNames.CONTENT_LENGTH, this.contentLength);
            }
         }

         if (this.isContentAlwaysEmpty(var2)) {
            HttpUtil.setTransferEncodingChunked(var2, false);
            return HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
         } else if (HttpUtil.isTransferEncodingChunked(var2)) {
            if (!var10.isEmpty() && var2.protocolVersion() == HttpVersion.HTTP_1_1) {
               this.handleTransferEncodingChunkedWithContentLength(var2);
            }

            return HttpObjectDecoder.State.READ_CHUNK_SIZE;
         } else {
            return this.contentLength() >= 0L ? HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT : HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;
         }
      }
   }

   protected void handleTransferEncodingChunkedWithContentLength(HttpMessage var1) {
      var1.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
      this.contentLength = Long.MIN_VALUE;
   }

   private long contentLength() {
      if (this.contentLength == Long.MIN_VALUE) {
         this.contentLength = HttpUtil.getContentLength(this.message, -1L);
      }

      return this.contentLength;
   }

   private LastHttpContent readTrailingHeaders(ByteBuf var1) {
      AppendableCharSequence var2 = this.headerParser.parse(var1);
      if (var2 == null) {
         return null;
      } else {
         Object var3 = this.trailer;
         if (var2.length() == 0 && var3 == null) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
         } else {
            CharSequence var4 = null;
            if (var3 == null) {
               var3 = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
            }

            while (var2.length() > 0) {
               char var5 = var2.charAtUnsafe(0);
               if (var4 != null && (var5 == ' ' || var5 == '\t')) {
                  List var10 = ((LastHttpContent)var3).trailingHeaders().getAll(var4);
                  if (!var10.isEmpty()) {
                     int var7 = var10.size() - 1;
                     String var8 = var2.toString().trim();
                     String var9 = (String)var10.get(var7);
                     var10.set(var7, var9 + var8);
                  }
               } else {
                  this.splitHeader(var2);
                  CharSequence var6 = this.name;
                  if (!HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(var6)
                     && !HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(var6)
                     && !HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(var6)) {
                     ((LastHttpContent)var3).trailingHeaders().add(var6, this.value);
                  }

                  var4 = this.name;
                  this.name = null;
                  this.value = null;
               }

               var2 = this.headerParser.parse(var1);
               if (var2 == null) {
                  return null;
               }
            }

            this.trailer = null;
            return (LastHttpContent)var3;
         }
      }
   }

   protected abstract boolean isDecodingRequest();

   protected abstract HttpMessage createMessage(String[] var1) throws Exception;

   protected abstract HttpMessage createInvalidMessage();

   private static int getChunkSize(String var0) {
      var0 = var0.trim();

      for (int var1 = 0; var1 < var0.length(); var1++) {
         char var2 = var0.charAt(var1);
         if (var2 == ';' || Character.isWhitespace(var2) || Character.isISOControl(var2)) {
            var0 = var0.substring(0, var1);
            break;
         }
      }

      return Integer.parseInt(var0, 16);
   }

   private static String[] splitInitialLine(AppendableCharSequence var0) {
      int var1 = findNonSPLenient(var0, 0);
      int var2 = findSPLenient(var0, var1);
      int var3 = findNonSPLenient(var0, var2);
      int var4 = findSPLenient(var0, var3);
      int var5 = findNonSPLenient(var0, var4);
      int var6 = findEndOfString(var0);
      return new String[]{var0.subStringUnsafe(var1, var2), var0.subStringUnsafe(var3, var4), var5 < var6 ? var0.subStringUnsafe(var5, var6) : ""};
   }

   private void splitHeader(AppendableCharSequence var1) {
      int var2 = var1.length();
      int var3 = findNonWhitespace(var1, 0);

      int var4;
      for (var4 = var3; var4 < var2; var4++) {
         char var8 = var1.charAtUnsafe(var4);
         if (var8 == ':' || !this.isDecodingRequest() && isOWS(var8)) {
            break;
         }
      }

      if (var4 == var2) {
         throw new IllegalArgumentException("No colon found");
      } else {
         int var5;
         for (var5 = var4; var5 < var2; var5++) {
            if (var1.charAtUnsafe(var5) == ':') {
               var5++;
               break;
            }
         }

         this.name = var1.subStringUnsafe(var3, var4);
         int var6 = findNonWhitespace(var1, var5);
         if (var6 == var2) {
            this.value = "";
         } else {
            int var7 = findEndOfString(var1);
            this.value = var1.subStringUnsafe(var6, var7);
         }
      }
   }

   private static int findNonSPLenient(AppendableCharSequence var0, int var1) {
      for (int var2 = var1; var2 < var0.length(); var2++) {
         char var3 = var0.charAtUnsafe(var2);
         if (!isSPLenient(var3)) {
            if (Character.isWhitespace(var3)) {
               throw new IllegalArgumentException("Invalid separator");
            }

            return var2;
         }
      }

      return var0.length();
   }

   private static int findSPLenient(AppendableCharSequence var0, int var1) {
      for (int var2 = var1; var2 < var0.length(); var2++) {
         if (isSPLenient(var0.charAtUnsafe(var2))) {
            return var2;
         }
      }

      return var0.length();
   }

   private static boolean isSPLenient(char var0) {
      return var0 == ' ' || var0 == '\t' || var0 == 11 || var0 == '\f' || var0 == '\r';
   }

   private static int findNonWhitespace(AppendableCharSequence var0, int var1) {
      for (int var2 = var1; var2 < var0.length(); var2++) {
         char var3 = var0.charAtUnsafe(var2);
         if (!Character.isWhitespace(var3)) {
            return var2;
         }

         if (!isOWS(var3)) {
            throw new IllegalArgumentException(
               "Invalid separator, only a single space or horizontal tab allowed, but received a '" + var3 + "' (0x" + Integer.toHexString(var3) + ")"
            );
         }
      }

      return var0.length();
   }

   private static int findEndOfString(AppendableCharSequence var0) {
      for (int var1 = var0.length() - 1; var1 > 0; var1--) {
         if (!Character.isWhitespace(var0.charAtUnsafe(var1))) {
            return var1 + 1;
         }
      }

      return 0;
   }

   private static boolean isOWS(char var0) {
      return var0 == ' ' || var0 == '\t';
   }

   private static class HeaderParser implements ByteProcessor {
      private final AppendableCharSequence seq;
      private final int maxLength;
      int size;

      HeaderParser(AppendableCharSequence var1, int var2) {
         this.seq = var1;
         this.maxLength = var2;
      }

      public AppendableCharSequence parse(ByteBuf var1) {
         int var2 = this.size;
         this.seq.reset();
         int var3 = var1.forEachByte(this);
         if (var3 == -1) {
            this.size = var2;
            return null;
         } else {
            var1.readerIndex(var3 + 1);
            return this.seq;
         }
      }

      public void reset() {
         this.size = 0;
      }

      @Override
      public boolean process(byte var1) throws Exception {
         char var2 = (char)(var1 & 255);
         if (var2 == '\n') {
            int var3 = this.seq.length();
            if (var3 >= 1 && this.seq.charAtUnsafe(var3 - 1) == '\r') {
               this.size--;
               this.seq.setLength(var3 - 1);
            }

            return false;
         } else {
            this.increaseCount();
            this.seq.append(var2);
            return true;
         }
      }

      protected final void increaseCount() {
         if (++this.size > this.maxLength) {
            throw this.newException(this.maxLength);
         }
      }

      protected TooLongFrameException newException(int var1) {
         return new TooLongHttpHeaderException("HTTP header is larger than " + var1 + " bytes.");
      }
   }

   private final class LineParser extends HttpObjectDecoder.HeaderParser {
      LineParser(AppendableCharSequence var2, int var3) {
         super(var2, var3);
      }

      @Override
      public AppendableCharSequence parse(ByteBuf var1) {
         this.reset();
         return super.parse(var1);
      }

      @Override
      public boolean process(byte var1) throws Exception {
         if (HttpObjectDecoder.this.currentState == HttpObjectDecoder.State.SKIP_CONTROL_CHARS) {
            char var2 = (char)(var1 & 255);
            if (Character.isISOControl(var2) || Character.isWhitespace(var2)) {
               this.increaseCount();
               return true;
            }

            HttpObjectDecoder.this.currentState = HttpObjectDecoder.State.READ_INITIAL;
         }

         return super.process(var1);
      }

      @Override
      protected TooLongFrameException newException(int var1) {
         return new TooLongHttpLineException("An HTTP line is larger than " + var1 + " bytes.");
      }
   }

   private static enum State {
      SKIP_CONTROL_CHARS,
      READ_INITIAL,
      READ_HEADER,
      READ_VARIABLE_LENGTH_CONTENT,
      READ_FIXED_LENGTH_CONTENT,
      READ_CHUNK_SIZE,
      READ_CHUNKED_CONTENT,
      READ_CHUNK_DELIMITER,
      READ_CHUNK_FOOTER,
      BAD_MESSAGE,
      UPGRADED;
   }
}
