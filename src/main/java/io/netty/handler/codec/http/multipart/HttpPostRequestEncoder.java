package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class HttpPostRequestEncoder implements ChunkedInput<HttpContent> {
   private static final Entry[] percentEncodings = new Entry[]{
      new SimpleImmutableEntry<>(Pattern.compile("\\*"), "%2A"),
      new SimpleImmutableEntry<>(Pattern.compile("\\+"), "%20"),
      new SimpleImmutableEntry<>(Pattern.compile("~"), "%7E")
   };
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final Charset charset;
   private boolean isChunked;
   private final List<InterfaceHttpData> bodyListDatas;
   final List<InterfaceHttpData> multipartHttpDatas;
   private final boolean isMultipart;
   String multipartDataBoundary;
   String multipartMixedBoundary;
   private boolean headerFinalized;
   private final HttpPostRequestEncoder.EncoderMode encoderMode;
   private boolean isLastChunk;
   private boolean isLastChunkSent;
   private FileUpload currentFileUpload;
   private boolean duringMixedMode;
   private long globalBodySize;
   private long globalProgress;
   private ListIterator<InterfaceHttpData> iterator;
   private ByteBuf currentBuffer;
   private InterfaceHttpData currentData;
   private boolean isKey = true;

   public HttpPostRequestEncoder(HttpRequest var1, boolean var2) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this(new DefaultHttpDataFactory(16384L), var1, var2, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
   }

   public HttpPostRequestEncoder(HttpDataFactory var1, HttpRequest var2, boolean var3) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this(var1, var2, var3, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
   }

   public HttpPostRequestEncoder(HttpDataFactory var1, HttpRequest var2, boolean var3, Charset var4, HttpPostRequestEncoder.EncoderMode var5) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this.request = ObjectUtil.checkNotNull(var2, "request");
      this.charset = ObjectUtil.checkNotNull(var4, "charset");
      this.factory = ObjectUtil.checkNotNull(var1, "factory");
      if (HttpMethod.TRACE.equals(var2.method())) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot create a Encoder if request is a TRACE");
      } else {
         this.bodyListDatas = new ArrayList<>();
         this.isLastChunk = false;
         this.isLastChunkSent = false;
         this.isMultipart = var3;
         this.multipartHttpDatas = new ArrayList<>();
         this.encoderMode = var5;
         if (this.isMultipart) {
            this.initDataMultipart();
         }
      }
   }

   public void cleanFiles() {
      this.factory.cleanRequestHttpData(this.request);
   }

   public boolean isMultipart() {
      return this.isMultipart;
   }

   private void initDataMultipart() {
      this.multipartDataBoundary = getNewMultipartDelimiter();
   }

   private void initMixedMultipart() {
      this.multipartMixedBoundary = getNewMultipartDelimiter();
   }

   private static String getNewMultipartDelimiter() {
      return Long.toHexString(PlatformDependent.threadLocalRandom().nextLong());
   }

   public List<InterfaceHttpData> getBodyListAttributes() {
      return this.bodyListDatas;
   }

   public void setBodyHttpDatas(List<InterfaceHttpData> var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      ObjectUtil.checkNotNull(var1, "datas");
      this.globalBodySize = 0L;
      this.bodyListDatas.clear();
      this.currentFileUpload = null;
      this.duringMixedMode = false;
      this.multipartHttpDatas.clear();

      for (InterfaceHttpData var3 : var1) {
         this.addBodyHttpData(var3);
      }
   }

   public void addBodyAttribute(String var1, String var2) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      String var3 = var2 != null ? var2 : "";
      Attribute var4 = this.factory.createAttribute(this.request, ObjectUtil.checkNotNull(var1, "name"), var3);
      this.addBodyHttpData(var4);
   }

   public void addBodyFileUpload(String var1, File var2, String var3, boolean var4) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this.addBodyFileUpload(var1, var2.getName(), var2, var3, var4);
   }

   public void addBodyFileUpload(String var1, String var2, File var3, String var4, boolean var5) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      ObjectUtil.checkNotNull(var1, "name");
      ObjectUtil.checkNotNull(var3, "file");
      if (var2 == null) {
         var2 = "";
      }

      String var6 = var4;
      String var7 = null;
      if (var4 == null) {
         if (var5) {
            var6 = "text/plain";
         } else {
            var6 = "application/octet-stream";
         }
      }

      if (!var5) {
         var7 = HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value();
      }

      FileUpload var8 = this.factory.createFileUpload(this.request, var1, var2, var6, var7, null, var3.length());

      try {
         var8.setContent(var3);
      } catch (IOException var10) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException(var10);
      }

      this.addBodyHttpData(var8);
   }

   public void addBodyFileUploads(String var1, File[] var2, String[] var3, boolean[] var4) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (var2.length != var3.length && var2.length != var4.length) {
         throw new IllegalArgumentException("Different array length");
      } else {
         for (int var5 = 0; var5 < var2.length; var5++) {
            this.addBodyFileUpload(var1, var2[var5], var3[var5], var4[var5]);
         }
      }
   }

   public void addBodyHttpData(InterfaceHttpData var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.headerFinalized) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot add value once finalized");
      } else {
         this.bodyListDatas.add(ObjectUtil.checkNotNull(var1, "data"));
         if (!this.isMultipart) {
            if (var1 instanceof Attribute) {
               Attribute var10 = (Attribute)var1;

               try {
                  String var13 = this.encodeAttribute(var10.getName(), this.charset);
                  String var16 = this.encodeAttribute(var10.getValue(), this.charset);
                  Attribute var19 = this.factory.createAttribute(this.request, var13, var16);
                  this.multipartHttpDatas.add(var19);
                  this.globalBodySize = this.globalBodySize + (long)(var19.getName().length() + 1) + var19.length() + 1L;
               } catch (IOException var7) {
                  throw new HttpPostRequestEncoder.ErrorDataEncoderException(var7);
               }
            } else if (var1 instanceof FileUpload) {
               FileUpload var11 = (FileUpload)var1;
               String var14 = this.encodeAttribute(var11.getName(), this.charset);
               String var17 = this.encodeAttribute(var11.getFilename(), this.charset);
               Attribute var20 = this.factory.createAttribute(this.request, var14, var17);
               this.multipartHttpDatas.add(var20);
               this.globalBodySize = this.globalBodySize + (long)(var20.getName().length() + 1) + var20.length() + 1L;
            }
         } else {
            if (var1 instanceof Attribute) {
               if (this.duringMixedMode) {
                  InternalAttribute var2 = new InternalAttribute(this.charset);
                  var2.addValue("\r\n--" + this.multipartMixedBoundary + "--");
                  this.multipartHttpDatas.add(var2);
                  this.multipartMixedBoundary = null;
                  this.currentFileUpload = null;
                  this.duringMixedMode = false;
               }

               InternalAttribute var8 = new InternalAttribute(this.charset);
               if (!this.multipartHttpDatas.isEmpty()) {
                  var8.addValue("\r\n");
               }

               var8.addValue("--" + this.multipartDataBoundary + "\r\n");
               Attribute var3 = (Attribute)var1;
               var8.addValue(
                  HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + var3.getName() + "\"\r\n"
               );
               var8.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + var3.length() + "\r\n");
               Charset var4 = var3.getCharset();
               if (var4 != null) {
                  var8.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + "text/plain" + "; " + HttpHeaderValues.CHARSET + '=' + var4.name() + "\r\n");
               }

               var8.addValue("\r\n");
               this.multipartHttpDatas.add(var8);
               this.multipartHttpDatas.add(var1);
               this.globalBodySize = this.globalBodySize + var3.length() + (long)var8.size();
            } else if (var1 instanceof FileUpload) {
               FileUpload var9 = (FileUpload)var1;
               InternalAttribute var12 = new InternalAttribute(this.charset);
               if (!this.multipartHttpDatas.isEmpty()) {
                  var12.addValue("\r\n");
               }

               boolean var15;
               if (this.duringMixedMode) {
                  if (this.currentFileUpload != null && this.currentFileUpload.getName().equals(var9.getName())) {
                     var15 = true;
                  } else {
                     var12.addValue("--" + this.multipartMixedBoundary + "--");
                     this.multipartHttpDatas.add(var12);
                     this.multipartMixedBoundary = null;
                     var12 = new InternalAttribute(this.charset);
                     var12.addValue("\r\n");
                     var15 = false;
                     this.currentFileUpload = var9;
                     this.duringMixedMode = false;
                  }
               } else if (this.encoderMode != HttpPostRequestEncoder.EncoderMode.HTML5
                  && this.currentFileUpload != null
                  && this.currentFileUpload.getName().equals(var9.getName())) {
                  this.initMixedMultipart();
                  InternalAttribute var5 = (InternalAttribute)this.multipartHttpDatas.get(this.multipartHttpDatas.size() - 2);
                  this.globalBodySize = this.globalBodySize - (long)var5.size();
                  StringBuilder var6 = new StringBuilder(
                        139
                           + this.multipartDataBoundary.length()
                           + this.multipartMixedBoundary.length() * 2
                           + var9.getFilename().length()
                           + var9.getName().length()
                     )
                     .append("--")
                     .append(this.multipartDataBoundary)
                     .append("\r\n")
                     .append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION)
                     .append(": ")
                     .append((CharSequence)HttpHeaderValues.FORM_DATA)
                     .append("; ")
                     .append((CharSequence)HttpHeaderValues.NAME)
                     .append("=\"")
                     .append(var9.getName())
                     .append("\"\r\n")
                     .append((CharSequence)HttpHeaderNames.CONTENT_TYPE)
                     .append(": ")
                     .append((CharSequence)HttpHeaderValues.MULTIPART_MIXED)
                     .append("; ")
                     .append((CharSequence)HttpHeaderValues.BOUNDARY)
                     .append('=')
                     .append(this.multipartMixedBoundary)
                     .append("\r\n\r\n")
                     .append("--")
                     .append(this.multipartMixedBoundary)
                     .append("\r\n")
                     .append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION)
                     .append(": ")
                     .append((CharSequence)HttpHeaderValues.ATTACHMENT);
                  if (!var9.getFilename().isEmpty()) {
                     var6.append("; ").append((CharSequence)HttpHeaderValues.FILENAME).append("=\"").append(this.currentFileUpload.getFilename()).append('"');
                  }

                  var6.append("\r\n");
                  var5.setValue(var6.toString(), 1);
                  var5.setValue("", 2);
                  this.globalBodySize = this.globalBodySize + (long)var5.size();
                  var15 = true;
                  this.duringMixedMode = true;
               } else {
                  var15 = false;
                  this.currentFileUpload = var9;
                  this.duringMixedMode = false;
               }

               if (var15) {
                  var12.addValue("--" + this.multipartMixedBoundary + "\r\n");
                  if (var9.getFilename().isEmpty()) {
                     var12.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "\r\n");
                  } else {
                     var12.addValue(
                        HttpHeaderNames.CONTENT_DISPOSITION
                           + ": "
                           + HttpHeaderValues.ATTACHMENT
                           + "; "
                           + HttpHeaderValues.FILENAME
                           + "=\""
                           + var9.getFilename()
                           + "\"\r\n"
                     );
                  }
               } else {
                  var12.addValue("--" + this.multipartDataBoundary + "\r\n");
                  if (var9.getFilename().isEmpty()) {
                     var12.addValue(
                        HttpHeaderNames.CONTENT_DISPOSITION
                           + ": "
                           + HttpHeaderValues.FORM_DATA
                           + "; "
                           + HttpHeaderValues.NAME
                           + "=\""
                           + var9.getName()
                           + "\"\r\n"
                     );
                  } else {
                     var12.addValue(
                        HttpHeaderNames.CONTENT_DISPOSITION
                           + ": "
                           + HttpHeaderValues.FORM_DATA
                           + "; "
                           + HttpHeaderValues.NAME
                           + "=\""
                           + var9.getName()
                           + "\"; "
                           + HttpHeaderValues.FILENAME
                           + "=\""
                           + var9.getFilename()
                           + "\"\r\n"
                     );
                  }
               }

               var12.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + var9.length() + "\r\n");
               var12.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + var9.getContentType());
               String var18 = var9.getContentTransferEncoding();
               if (var18 != null && var18.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                  var12.addValue(
                     "\r\n" + HttpHeaderNames.CONTENT_TRANSFER_ENCODING + ": " + HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value() + "\r\n\r\n"
                  );
               } else if (var9.getCharset() != null) {
                  var12.addValue("; " + HttpHeaderValues.CHARSET + '=' + var9.getCharset().name() + "\r\n\r\n");
               } else {
                  var12.addValue("\r\n\r\n");
               }

               this.multipartHttpDatas.add(var12);
               this.multipartHttpDatas.add(var1);
               this.globalBodySize = this.globalBodySize + var9.length() + (long)var12.size();
            }
         }
      }
   }

   public HttpRequest finalizeRequest() throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (!this.headerFinalized) {
         if (this.isMultipart) {
            InternalAttribute var1 = new InternalAttribute(this.charset);
            if (this.duringMixedMode) {
               var1.addValue("\r\n--" + this.multipartMixedBoundary + "--");
            }

            var1.addValue("\r\n--" + this.multipartDataBoundary + "--\r\n");
            this.multipartHttpDatas.add(var1);
            this.multipartMixedBoundary = null;
            this.currentFileUpload = null;
            this.duringMixedMode = false;
            this.globalBodySize = this.globalBodySize + (long)var1.size();
         }

         this.headerFinalized = true;
         HttpHeaders var9 = this.request.headers();
         List var2 = var9.getAll(HttpHeaderNames.CONTENT_TYPE);
         List var3 = var9.getAll(HttpHeaderNames.TRANSFER_ENCODING);
         if (var2 != null) {
            var9.remove(HttpHeaderNames.CONTENT_TYPE);

            for (String var5 : var2) {
               String var6 = var5.toLowerCase();
               if (!var6.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString())
                  && !var6.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                  var9.add(HttpHeaderNames.CONTENT_TYPE, var5);
               }
            }
         }

         if (this.isMultipart) {
            String var10 = HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + '=' + this.multipartDataBoundary;
            var9.add(HttpHeaderNames.CONTENT_TYPE, var10);
         } else {
            var9.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
         }

         long var11 = this.globalBodySize;
         if (!this.isMultipart) {
            var11--;
         }

         this.iterator = this.multipartHttpDatas.listIterator();
         var9.set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(var11));
         if (var11 <= 8096L && !this.isMultipart) {
            HttpContent var13 = this.nextChunk();
            if (this.request instanceof FullHttpRequest) {
               FullHttpRequest var14 = (FullHttpRequest)this.request;
               ByteBuf var8 = var13.content();
               if (var14.content() != var8) {
                  var14.content().clear().writeBytes(var8);
                  var8.release();
               }

               return var14;
            } else {
               return new HttpPostRequestEncoder.WrappedFullHttpRequest(this.request, var13);
            }
         } else {
            this.isChunked = true;
            if (var3 != null) {
               var9.remove(HttpHeaderNames.TRANSFER_ENCODING);

               for (CharSequence var7 : var3) {
                  if (!HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(var7)) {
                     var9.add(HttpHeaderNames.TRANSFER_ENCODING, var7);
                  }
               }
            }

            HttpUtil.setTransferEncodingChunked(this.request, true);
            return new HttpPostRequestEncoder.WrappedHttpRequest(this.request);
         }
      } else {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Header already encoded");
      }
   }

   public boolean isChunked() {
      return this.isChunked;
   }

   private String encodeAttribute(String var1, Charset var2) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (var1 == null) {
         return "";
      } else {
         try {
            String var3 = URLEncoder.encode(var1, var2.name());
            if (this.encoderMode == HttpPostRequestEncoder.EncoderMode.RFC3986) {
               for (Entry var7 : percentEncodings) {
                  String var8 = (String)var7.getValue();
                  var3 = ((Pattern)var7.getKey()).matcher(var3).replaceAll(var8);
               }
            }

            return var3;
         } catch (UnsupportedEncodingException var9) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException(var2.name(), var9);
         }
      }
   }

   private ByteBuf fillByteBuf() {
      int var1 = this.currentBuffer.readableBytes();
      if (var1 > 8096) {
         return this.currentBuffer.readRetainedSlice(8096);
      } else {
         ByteBuf var2 = this.currentBuffer;
         this.currentBuffer = null;
         return var2;
      }
   }

   private HttpContent encodeNextChunkMultipart(int var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.currentData == null) {
         return null;
      } else {
         ByteBuf var2;
         if (this.currentData instanceof InternalAttribute) {
            var2 = ((InternalAttribute)this.currentData).toByteBuf();
            this.currentData = null;
         } else {
            try {
               var2 = ((HttpData)this.currentData).getChunk(var1);
            } catch (IOException var4) {
               throw new HttpPostRequestEncoder.ErrorDataEncoderException(var4);
            }

            if (var2.capacity() == 0) {
               this.currentData = null;
               return null;
            }
         }

         if (this.currentBuffer == null) {
            this.currentBuffer = var2;
         } else {
            this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var2);
         }

         if (this.currentBuffer.readableBytes() < 8096) {
            this.currentData = null;
            return null;
         } else {
            var2 = this.fillByteBuf();
            return new DefaultHttpContent(var2);
         }
      }
   }

   private HttpContent encodeNextChunkUrlEncoded(int var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.currentData == null) {
         return null;
      } else {
         int var2 = var1;
         if (this.isKey) {
            String var4 = this.currentData.getName();
            ByteBuf var3 = Unpooled.wrappedBuffer(var4.getBytes(this.charset));
            this.isKey = false;
            if (this.currentBuffer == null) {
               this.currentBuffer = Unpooled.wrappedBuffer(var3, Unpooled.wrappedBuffer("=".getBytes(this.charset)));
            } else {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var3, Unpooled.wrappedBuffer("=".getBytes(this.charset)));
            }

            var2 = var1 - (var3.readableBytes() + 1);
            if (this.currentBuffer.readableBytes() >= 8096) {
               var3 = this.fillByteBuf();
               return new DefaultHttpContent(var3);
            }
         }

         ByteBuf var6;
         try {
            var6 = ((HttpData)this.currentData).getChunk(var2);
         } catch (IOException var5) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException(var5);
         }

         ByteBuf var10 = null;
         if (var6.readableBytes() < var2) {
            this.isKey = true;
            var10 = this.iterator.hasNext() ? Unpooled.wrappedBuffer("&".getBytes(this.charset)) : null;
         }

         if (var6.capacity() == 0) {
            this.currentData = null;
            if (this.currentBuffer == null) {
               if (var10 == null) {
                  return null;
               }

               this.currentBuffer = var10;
            } else if (var10 != null) {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var10);
            }

            if (this.currentBuffer.readableBytes() >= 8096) {
               var6 = this.fillByteBuf();
               return new DefaultHttpContent(var6);
            } else {
               return null;
            }
         } else {
            if (this.currentBuffer == null) {
               if (var10 != null) {
                  this.currentBuffer = Unpooled.wrappedBuffer(var6, var10);
               } else {
                  this.currentBuffer = var6;
               }
            } else if (var10 != null) {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var6, var10);
            } else {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var6);
            }

            if (this.currentBuffer.readableBytes() < 8096) {
               this.currentData = null;
               this.isKey = true;
               return null;
            } else {
               var6 = this.fillByteBuf();
               return new DefaultHttpContent(var6);
            }
         }
      }
   }

   @Override
   public void close() throws Exception {
   }

   @Deprecated
   public HttpContent readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public HttpContent readChunk(ByteBufAllocator var1) throws Exception {
      if (this.isLastChunkSent) {
         return null;
      } else {
         HttpContent var2 = this.nextChunk();
         this.globalProgress = this.globalProgress + (long)var2.content().readableBytes();
         return var2;
      }
   }

   private HttpContent nextChunk() throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.isLastChunk) {
         this.isLastChunkSent = true;
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         int var1 = this.calculateRemainingSize();
         if (var1 <= 0) {
            ByteBuf var4 = this.fillByteBuf();
            return new DefaultHttpContent(var4);
         } else {
            if (this.currentData != null) {
               HttpContent var2;
               if (this.isMultipart) {
                  var2 = this.encodeNextChunkMultipart(var1);
               } else {
                  var2 = this.encodeNextChunkUrlEncoded(var1);
               }

               if (var2 != null) {
                  return var2;
               }

               var1 = this.calculateRemainingSize();
            }

            if (!this.iterator.hasNext()) {
               return this.lastChunk();
            } else {
               while (var1 > 0 && this.iterator.hasNext()) {
                  this.currentData = this.iterator.next();
                  HttpContent var3;
                  if (this.isMultipart) {
                     var3 = this.encodeNextChunkMultipart(var1);
                  } else {
                     var3 = this.encodeNextChunkUrlEncoded(var1);
                  }

                  if (var3 != null) {
                     return var3;
                  }

                  var1 = this.calculateRemainingSize();
               }

               return this.lastChunk();
            }
         }
      }
   }

   private int calculateRemainingSize() {
      int var1 = 8096;
      if (this.currentBuffer != null) {
         var1 -= this.currentBuffer.readableBytes();
      }

      return var1;
   }

   private HttpContent lastChunk() {
      this.isLastChunk = true;
      if (this.currentBuffer == null) {
         this.isLastChunkSent = true;
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         ByteBuf var1 = this.currentBuffer;
         this.currentBuffer = null;
         return new DefaultHttpContent(var1);
      }
   }

   @Override
   public boolean isEndOfInput() throws Exception {
      return this.isLastChunkSent;
   }

   @Override
   public long length() {
      return this.isMultipart ? this.globalBodySize : this.globalBodySize - 1L;
   }

   @Override
   public long progress() {
      return this.globalProgress;
   }

   public static enum EncoderMode {
      RFC1738,
      RFC3986,
      HTML5;
   }

   public static class ErrorDataEncoderException extends Exception {
      private static final long serialVersionUID = 5020247425493164465L;

      public ErrorDataEncoderException() {
      }

      public ErrorDataEncoderException(String var1) {
         super(var1);
      }

      public ErrorDataEncoderException(Throwable var1) {
         super(var1);
      }

      public ErrorDataEncoderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   private static final class WrappedFullHttpRequest extends HttpPostRequestEncoder.WrappedHttpRequest implements FullHttpRequest {
      private final HttpContent content;

      private WrappedFullHttpRequest(HttpRequest var1, HttpContent var2) {
         super(var1);
         this.content = var2;
      }

      @Override
      public FullHttpRequest setProtocolVersion(HttpVersion var1) {
         super.setProtocolVersion(var1);
         return this;
      }

      @Override
      public FullHttpRequest setMethod(HttpMethod var1) {
         super.setMethod(var1);
         return this;
      }

      @Override
      public FullHttpRequest setUri(String var1) {
         super.setUri(var1);
         return this;
      }

      @Override
      public FullHttpRequest copy() {
         return this.replace(this.content().copy());
      }

      @Override
      public FullHttpRequest duplicate() {
         return this.replace(this.content().duplicate());
      }

      @Override
      public FullHttpRequest retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      @Override
      public FullHttpRequest replace(ByteBuf var1) {
         DefaultFullHttpRequest var2 = new DefaultFullHttpRequest(this.protocolVersion(), this.method(), this.uri(), var1);
         var2.headers().set(this.headers());
         var2.trailingHeaders().set(this.trailingHeaders());
         return var2;
      }

      @Override
      public FullHttpRequest retain(int var1) {
         this.content.retain(var1);
         return this;
      }

      @Override
      public FullHttpRequest retain() {
         this.content.retain();
         return this;
      }

      @Override
      public FullHttpRequest touch() {
         this.content.touch();
         return this;
      }

      @Override
      public FullHttpRequest touch(Object var1) {
         this.content.touch(var1);
         return this;
      }

      @Override
      public ByteBuf content() {
         return this.content.content();
      }

      @Override
      public HttpHeaders trailingHeaders() {
         return (HttpHeaders)(this.content instanceof LastHttpContent ? ((LastHttpContent)this.content).trailingHeaders() : EmptyHttpHeaders.INSTANCE);
      }

      @Override
      public int refCnt() {
         return this.content.refCnt();
      }

      @Override
      public boolean release() {
         return this.content.release();
      }

      @Override
      public boolean release(int var1) {
         return this.content.release(var1);
      }
   }

   private static class WrappedHttpRequest implements HttpRequest {
      private final HttpRequest request;

      WrappedHttpRequest(HttpRequest var1) {
         this.request = var1;
      }

      @Override
      public HttpRequest setProtocolVersion(HttpVersion var1) {
         this.request.setProtocolVersion(var1);
         return this;
      }

      @Override
      public HttpRequest setMethod(HttpMethod var1) {
         this.request.setMethod(var1);
         return this;
      }

      @Override
      public HttpRequest setUri(String var1) {
         this.request.setUri(var1);
         return this;
      }

      @Override
      public HttpMethod getMethod() {
         return this.request.method();
      }

      @Override
      public HttpMethod method() {
         return this.request.method();
      }

      @Override
      public String getUri() {
         return this.request.uri();
      }

      @Override
      public String uri() {
         return this.request.uri();
      }

      @Override
      public HttpVersion getProtocolVersion() {
         return this.request.protocolVersion();
      }

      @Override
      public HttpVersion protocolVersion() {
         return this.request.protocolVersion();
      }

      @Override
      public HttpHeaders headers() {
         return this.request.headers();
      }

      @Override
      public DecoderResult decoderResult() {
         return this.request.decoderResult();
      }

      @Deprecated
      @Override
      public DecoderResult getDecoderResult() {
         return this.request.getDecoderResult();
      }

      @Override
      public void setDecoderResult(DecoderResult var1) {
         this.request.setDecoderResult(var1);
      }
   }
}
