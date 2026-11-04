package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostMultipartRequestDecoder implements InterfaceHttpPostRequestDecoder {
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private Charset charset;
   private boolean isLastChunk;
   private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<>();
   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<>(CaseIgnoringComparator.INSTANCE);
   private ByteBuf undecodedChunk;
   private int bodyListHttpDataRank;
   private final String multipartDataBoundary;
   private String multipartMixedBoundary;
   private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
   private Map<CharSequence, Attribute> currentFieldAttributes;
   private FileUpload currentFileUpload;
   private Attribute currentAttribute;
   private boolean destroyed;
   private int discardThreshold = 10485760;
   private static final String FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';

   public HttpPostMultipartRequestDecoder(HttpRequest var1) {
      this(new DefaultHttpDataFactory(16384L), var1, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory var1, HttpRequest var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory var1, HttpRequest var2, Charset var3) {
      this.request = ObjectUtil.checkNotNull(var2, "request");
      this.charset = ObjectUtil.checkNotNull(var3, "charset");
      this.factory = ObjectUtil.checkNotNull(var1, "factory");
      String var4 = this.request.headers().get(HttpHeaderNames.CONTENT_TYPE);
      if (var4 == null) {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("No '" + HttpHeaderNames.CONTENT_TYPE + "' header present.");
      } else {
         String[] var5 = HttpPostRequestDecoder.getMultipartDataBoundary(var4);
         if (var5 != null) {
            this.multipartDataBoundary = var5[0];
            if (var5.length > 1 && var5[1] != null) {
               try {
                  this.charset = Charset.forName(var5[1]);
               } catch (IllegalCharsetNameException var8) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var8);
               }
            }
         } else {
            this.multipartDataBoundary = null;
         }

         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;

         try {
            if (var2 instanceof HttpContent) {
               this.offer((HttpContent)var2);
            } else {
               this.parseBody();
            }
         } catch (Throwable var7) {
            this.destroy();
            PlatformDependent.throwException(var7);
         }
      }
   }

   private void checkDestroyed() {
      if (this.destroyed) {
         throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
      }
   }

   @Override
   public boolean isMultipart() {
      this.checkDestroyed();
      return true;
   }

   @Override
   public void setDiscardThreshold(int var1) {
      this.discardThreshold = ObjectUtil.checkPositiveOrZero(var1, "discardThreshold");
   }

   @Override
   public int getDiscardThreshold() {
      return this.discardThreshold;
   }

   @Override
   public List<InterfaceHttpData> getBodyHttpDatas() {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return this.bodyListHttpData;
      }
   }

   @Override
   public List<InterfaceHttpData> getBodyHttpDatas(String var1) {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return this.bodyMapHttpData.get(var1);
      }
   }

   @Override
   public InterfaceHttpData getBodyHttpData(String var1) {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         List var2 = this.bodyMapHttpData.get(var1);
         return var2 != null ? (InterfaceHttpData)var2.get(0) : null;
      }
   }

   public HttpPostMultipartRequestDecoder offer(HttpContent var1) {
      this.checkDestroyed();
      if (var1 instanceof LastHttpContent) {
         this.isLastChunk = true;
      }

      ByteBuf var2 = var1.content();
      if (this.undecodedChunk == null) {
         this.undecodedChunk = var2.alloc().buffer(var2.readableBytes()).writeBytes(var2);
      } else {
         this.undecodedChunk.writeBytes(var2);
      }

      this.parseBody();
      if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
         if (this.undecodedChunk.refCnt() == 1) {
            this.undecodedChunk.discardReadBytes();
         } else {
            ByteBuf var3 = this.undecodedChunk.alloc().buffer(this.undecodedChunk.readableBytes());
            var3.writeBytes(this.undecodedChunk);
            this.undecodedChunk.release();
            this.undecodedChunk = var3;
         }
      }

      return this;
   }

   @Override
   public boolean hasNext() {
      this.checkDestroyed();
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
         throw new HttpPostRequestDecoder.EndOfDataDecoderException();
      } else {
         return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
      }
   }

   @Override
   public InterfaceHttpData next() {
      this.checkDestroyed();
      return this.hasNext() ? this.bodyListHttpData.get(this.bodyListHttpDataRank++) : null;
   }

   @Override
   public InterfaceHttpData currentPartialHttpData() {
      return (InterfaceHttpData)(this.currentFileUpload != null ? this.currentFileUpload : this.currentAttribute);
   }

   private void parseBody() {
      if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
         this.parseBodyMultipart();
      } else {
         if (this.isLastChunk) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
         }
      }
   }

   protected void addHttpData(InterfaceHttpData var1) {
      if (var1 != null) {
         Object var2 = this.bodyMapHttpData.get(var1.getName());
         if (var2 == null) {
            var2 = new ArrayList(1);
            this.bodyMapHttpData.put(var1.getName(), (List<InterfaceHttpData>)var2);
         }

         var2.add(var1);
         this.bodyListHttpData.add(var1);
      }
   }

   private void parseBodyMultipart() {
      if (this.undecodedChunk != null && this.undecodedChunk.readableBytes() != 0) {
         for (InterfaceHttpData var1 = this.decodeMultipart(this.currentStatus); var1 != null; var1 = this.decodeMultipart(this.currentStatus)) {
            this.addHttpData(var1);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE
               || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
               break;
            }
         }
      }
   }

   private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus var1) {
      switch (var1) {
         case NOTSTARTED:
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
         case PREAMBLE:
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
         case HEADERDELIMITER:
            return this.findMultipartDelimiter(
               this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE
            );
         case DISPOSITION:
            return this.findMultipartDisposition();
         case FIELD:
            Charset var2 = null;
            Attribute var3 = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
            if (var3 != null) {
               try {
                  var2 = Charset.forName(var3.getValue());
               } catch (IOException var14) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
               } catch (UnsupportedCharsetException var15) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
               }
            }

            Attribute var4 = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
            if (this.currentAttribute == null) {
               Attribute var5 = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);

               long var6;
               try {
                  var6 = var5 != null ? Long.parseLong(var5.getValue()) : 0L;
               } catch (IOException var12) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
               } catch (NumberFormatException var13) {
                  var6 = 0L;
               }

               try {
                  if (var6 > 0L) {
                     this.currentAttribute = this.factory.createAttribute(this.request, cleanString(var4.getValue()), var6);
                  } else {
                     this.currentAttribute = this.factory.createAttribute(this.request, cleanString(var4.getValue()));
                  }
               } catch (NullPointerException var9) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
               } catch (IllegalArgumentException var10) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
               } catch (IOException var11) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
               }

               if (var2 != null) {
                  this.currentAttribute.setCharset(var2);
               }
            }

            if (!loadDataMultipartOptimized(this.undecodedChunk, this.multipartDataBoundary, this.currentAttribute)) {
               return null;
            }

            Attribute var16 = this.currentAttribute;
            this.currentAttribute = null;
            this.currentFieldAttributes = null;
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            return var16;
         case FILEUPLOAD:
            return this.getFileUpload(this.multipartDataBoundary);
         case MIXEDDELIMITER:
            return this.findMultipartDelimiter(
               this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER
            );
         case MIXEDDISPOSITION:
            return this.findMultipartDisposition();
         case MIXEDFILEUPLOAD:
            return this.getFileUpload(this.multipartMixedBoundary);
         case PREEPILOGUE:
            return null;
         case EPILOGUE:
            return null;
         default:
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
      }
   }

   private static void skipControlCharacters(ByteBuf var0) {
      if (!var0.hasArray()) {
         try {
            skipControlCharactersStandard(var0);
         } catch (IndexOutOfBoundsException var3) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var3);
         }
      } else {
         HttpPostBodyUtil.SeekAheadOptimize var1 = new HttpPostBodyUtil.SeekAheadOptimize(var0);

         while (var1.pos < var1.limit) {
            char var2 = (char)(var1.bytes[var1.pos++] & 255);
            if (!Character.isISOControl(var2) && !Character.isWhitespace(var2)) {
               var1.setReadPosition(1);
               return;
            }
         }

         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
      }
   }

   private static void skipControlCharactersStandard(ByteBuf var0) {
      char var1;
      do {
         var1 = (char)var0.readUnsignedByte();
      } while (Character.isISOControl(var1) || Character.isWhitespace(var1));

      var0.readerIndex(var0.readerIndex() - 1);
   }

   private InterfaceHttpData findMultipartDelimiter(String var1, HttpPostRequestDecoder.MultiPartStatus var2, HttpPostRequestDecoder.MultiPartStatus var3) {
      int var4 = this.undecodedChunk.readerIndex();

      try {
         skipControlCharacters(this.undecodedChunk);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var8) {
         this.undecodedChunk.readerIndex(var4);
         return null;
      }

      this.skipOneLine();

      String var5;
      try {
         var5 = readDelimiterOptimized(this.undecodedChunk, var1, this.charset);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var7) {
         this.undecodedChunk.readerIndex(var4);
         return null;
      }

      if (var5.equals(var1)) {
         this.currentStatus = var2;
         return this.decodeMultipart(var2);
      } else if (var5.equals(var1 + "--")) {
         this.currentStatus = var3;
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
            this.currentFieldAttributes = null;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
         } else {
            return null;
         }
      } else {
         this.undecodedChunk.readerIndex(var4);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
      }
   }

   private InterfaceHttpData findMultipartDisposition() {
      int var1 = this.undecodedChunk.readerIndex();
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         this.currentFieldAttributes = new TreeMap<>(CaseIgnoringComparator.INSTANCE);
      }

      while (!this.skipOneLine()) {
         String var2;
         try {
            skipControlCharacters(this.undecodedChunk);
            var2 = readLineOptimized(this.undecodedChunk, this.charset);
         } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var22) {
            this.undecodedChunk.readerIndex(var1);
            return null;
         }

         String[] var3 = splitMultipartHeader(var2);
         if (!HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(var3[0])) {
            if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(var3[0])) {
               Attribute var27;
               try {
                  var27 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), cleanString(var3[1]));
               } catch (NullPointerException var18) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var18);
               } catch (IllegalArgumentException var19) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var19);
               }

               this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, var27);
            } else if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(var3[0])) {
               Attribute var26;
               try {
                  var26 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_LENGTH.toString(), cleanString(var3[1]));
               } catch (NullPointerException var16) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
               } catch (IllegalArgumentException var17) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var17);
               }

               this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, var26);
            } else if (HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(var3[0])) {
               if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(var3[1])) {
                  if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                     String var25 = StringUtil.substringAfter(var3[2], '=');
                     this.multipartMixedBoundary = "--" + var25;
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                     return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                  }

                  throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
               }

               for (int var24 = 1; var24 < var3.length; var24++) {
                  String var28 = HttpHeaderValues.CHARSET.toString();
                  if (var3[var24].regionMatches(true, 0, var28, 0, var28.length())) {
                     String var29 = StringUtil.substringAfter(var3[var24], '=');

                     Attribute var32;
                     try {
                        var32 = this.factory.createAttribute(this.request, var28, cleanString(var29));
                     } catch (NullPointerException var14) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
                     } catch (IllegalArgumentException var15) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
                     }

                     this.currentFieldAttributes.put(HttpHeaderValues.CHARSET, var32);
                  } else if (var3[var24].contains("=")) {
                     String var30 = StringUtil.substringBefore(var3[var24], '=');
                     String var33 = StringUtil.substringAfter(var3[var24], '=');

                     Attribute var8;
                     try {
                        var8 = this.factory.createAttribute(this.request, cleanString(var30), var33);
                     } catch (NullPointerException var12) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
                     } catch (IllegalArgumentException var13) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
                     }

                     this.currentFieldAttributes.put(var30, var8);
                  } else {
                     Attribute var31;
                     try {
                        var31 = this.factory.createAttribute(this.request, cleanString(var3[0]), var3[var24]);
                     } catch (NullPointerException var10) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
                     } catch (IllegalArgumentException var11) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
                     }

                     this.currentFieldAttributes.put(var31.getName(), var31);
                  }
               }
            }
         } else {
            boolean var4;
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
               var4 = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(var3[1]);
            } else {
               var4 = HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(var3[1]) || HttpHeaderValues.FILE.contentEqualsIgnoreCase(var3[1]);
            }

            if (var4) {
               for (int var5 = 2; var5 < var3.length; var5++) {
                  String[] var6 = var3[var5].split("=", 2);

                  Attribute var7;
                  try {
                     var7 = this.getContentDispositionAttribute(var6);
                  } catch (NullPointerException var20) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var20);
                  } catch (IllegalArgumentException var21) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var21);
                  }

                  this.currentFieldAttributes.put(var7.getName(), var7);
               }
            }
         }
      }

      Attribute var23 = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         if (var23 != null) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
         }
      } else if (var23 != null) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
         return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
      } else {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
      }
   }

   private Attribute getContentDispositionAttribute(String... var1) {
      String var2 = cleanString(var1[0]);
      String var3 = var1[1];
      if (HttpHeaderValues.FILENAME.contentEquals(var2)) {
         int var4 = var3.length() - 1;
         if (var4 > 0 && var3.charAt(0) == '"' && var3.charAt(var4) == '"') {
            var3 = var3.substring(1, var4);
         }
      } else if (FILENAME_ENCODED.equals(var2)) {
         try {
            var2 = HttpHeaderValues.FILENAME.toString();
            String[] var7 = cleanString(var3).split("'", 3);
            var3 = QueryStringDecoder.decodeComponent(var7[2], Charset.forName(var7[0]));
         } catch (ArrayIndexOutOfBoundsException var5) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var5);
         } catch (UnsupportedCharsetException var6) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var6);
         }
      } else {
         var3 = cleanString(var3);
      }

      return this.factory.createAttribute(this.request, var2, var3);
   }

   protected InterfaceHttpData getFileUpload(String var1) {
      Attribute var2 = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
      Charset var3 = this.charset;
      HttpPostBodyUtil.TransferEncodingMechanism var4 = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
      if (var2 != null) {
         String var5;
         try {
            var5 = var2.getValue().toLowerCase();
         } catch (IOException var20) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var20);
         }

         if (var5.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
            var3 = CharsetUtil.US_ASCII;
         } else if (var5.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
            var3 = CharsetUtil.ISO_8859_1;
            var4 = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
         } else {
            if (!var5.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + var5);
            }

            var4 = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
         }
      }

      Attribute var21 = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
      if (var21 != null) {
         try {
            var3 = Charset.forName(var21.getValue());
         } catch (IOException var18) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var18);
         } catch (UnsupportedCharsetException var19) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var19);
         }
      }

      if (this.currentFileUpload == null) {
         Attribute var6 = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
         Attribute var7 = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
         Attribute var8 = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
         Attribute var9 = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);

         long var10;
         try {
            var10 = var9 != null ? Long.parseLong(var9.getValue()) : 0L;
         } catch (IOException var16) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
         } catch (NumberFormatException var17) {
            var10 = 0L;
         }

         try {
            String var12;
            if (var8 != null) {
               var12 = var8.getValue();
            } else {
               var12 = "application/octet-stream";
            }

            this.currentFileUpload = this.factory
               .createFileUpload(this.request, cleanString(var7.getValue()), cleanString(var6.getValue()), var12, var4.value(), var3, var10);
         } catch (NullPointerException var13) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
         } catch (IllegalArgumentException var14) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
         } catch (IOException var15) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
         }
      }

      if (!loadDataMultipartOptimized(this.undecodedChunk, var1, this.currentFileUpload)) {
         return null;
      } else if (this.currentFileUpload.isCompleted()) {
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            this.currentFieldAttributes = null;
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
            this.cleanMixedAttributes();
         }

         FileUpload var22 = this.currentFileUpload;
         this.currentFileUpload = null;
         return var22;
      } else {
         return null;
      }
   }

   @Override
   public void destroy() {
      this.cleanFiles();

      for (InterfaceHttpData var2 : this.bodyListHttpData) {
         if (var2.refCnt() > 0) {
            var2.release();
         }
      }

      this.destroyed = true;
      if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
         this.undecodedChunk.release();
         this.undecodedChunk = null;
      }
   }

   @Override
   public void cleanFiles() {
      this.checkDestroyed();
      this.factory.cleanRequestHttpData(this.request);
   }

   @Override
   public void removeHttpDataFromClean(InterfaceHttpData var1) {
      this.checkDestroyed();
      this.factory.removeHttpDataFromClean(this.request, var1);
   }

   private void cleanMixedAttributes() {
      this.currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
      this.currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
   }

   private static String readLineOptimized(ByteBuf var0, Charset var1) {
      int var2 = var0.readerIndex();
      ByteBuf var3 = null;

      try {
         if (var0.isReadable()) {
            int var4 = HttpPostBodyUtil.findLineBreak(var0, var0.readerIndex());
            if (var4 <= 0) {
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            String var6;
            try {
               var3 = var0.alloc().heapBuffer(var4);
               var3.writeBytes(var0, var4);
               byte var5 = var0.readByte();
               if (var5 == 13) {
                  var0.readByte();
               }

               var6 = var3.toString(var1);
            } finally {
               var3.release();
            }

            return var6;
         }
      } catch (IndexOutOfBoundsException var11) {
         var0.readerIndex(var2);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var11);
      }

      var0.readerIndex(var2);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private static String readDelimiterOptimized(ByteBuf var0, String var1, Charset var2) {
      int var3 = var0.readerIndex();
      byte[] var4 = var1.getBytes(var2);
      int var5 = var4.length;

      try {
         int var6 = HttpPostBodyUtil.findDelimiter(var0, var3, var4, false);
         if (var6 < 0) {
            var0.readerIndex(var3);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
         }

         StringBuilder var7 = new StringBuilder(var1);
         var0.readerIndex(var3 + var6 + var5);
         if (var0.isReadable()) {
            byte var8 = var0.readByte();
            if (var8 == 13) {
               var8 = var0.readByte();
               if (var8 == 10) {
                  return var7.toString();
               }

               var0.readerIndex(var3);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            if (var8 == 10) {
               return var7.toString();
            }

            if (var8 == 45) {
               var7.append('-');
               var8 = var0.readByte();
               if (var8 == 45) {
                  var7.append('-');
                  if (var0.isReadable()) {
                     var8 = var0.readByte();
                     if (var8 == 13) {
                        var8 = var0.readByte();
                        if (var8 == 10) {
                           return var7.toString();
                        }

                        var0.readerIndex(var3);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                     }

                     if (var8 == 10) {
                        return var7.toString();
                     }

                     var0.readerIndex(var0.readerIndex() - 1);
                     return var7.toString();
                  }

                  return var7.toString();
               }
            }
         }
      } catch (IndexOutOfBoundsException var9) {
         var0.readerIndex(var3);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var9);
      }

      var0.readerIndex(var3);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private static void rewriteCurrentBuffer(ByteBuf var0, int var1) {
      if (var1 != 0) {
         int var2 = var0.readerIndex();
         int var3 = var0.readableBytes();
         if (var3 == var1) {
            var0.readerIndex(var2);
            var0.writerIndex(var2);
         } else {
            var0.setBytes(var2, var0, var2 + var1, var3 - var1);
            var0.readerIndex(var2);
            var0.writerIndex(var2 + var3 - var1);
         }
      }
   }

   private static boolean loadDataMultipartOptimized(ByteBuf var0, String var1, HttpData var2) {
      if (!var0.isReadable()) {
         return false;
      } else {
         int var3 = var0.readerIndex();
         byte[] var4 = var1.getBytes(var2.getCharset());
         int var5 = HttpPostBodyUtil.findDelimiter(var0, var3, var4, true);
         if (var5 < 0) {
            int var15 = var0.readableBytes();
            int var7 = var15 - var4.length - 1;
            if (var7 < 0) {
               var7 = 0;
            }

            var5 = HttpPostBodyUtil.findLastLineBreak(var0, var3 + var7);
            if (var5 < 0 && var2.definedLength() == var2.length() + (long)var15 - 1L && var0.getByte(var15 + var3 - 1) == 13) {
               var7 = 0;
               var5 = var15 - 1;
            }

            if (var5 < 0) {
               ByteBuf var16 = var0.copy();

               try {
                  var2.addContent(var16, false);
               } catch (IOException var10) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
               }

               var0.readerIndex(var3);
               var0.writerIndex(var3);
               return false;
            } else {
               var5 += var7;
               if (var5 == 0) {
                  return false;
               } else {
                  ByteBuf var8 = var0.copy(var3, var5);

                  try {
                     var2.addContent(var8, false);
                  } catch (IOException var11) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
                  }

                  rewriteCurrentBuffer(var0, var5);
                  return false;
               }
            }
         } else {
            ByteBuf var6 = var0.copy(var3, var5);

            try {
               var2.addContent(var6, true);
            } catch (IOException var12) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
            }

            rewriteCurrentBuffer(var0, var5);
            return true;
         }
      }
   }

   private static String cleanString(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = new StringBuilder(var1);

      for (int var3 = 0; var3 < var1; var3++) {
         char var4 = var0.charAt(var3);
         switch (var4) {
            case '\t':
            case ',':
            case ':':
            case ';':
            case '=':
               var2.append(' ');
            case '"':
               break;
            default:
               var2.append(var4);
         }
      }

      return var2.toString().trim();
   }

   private boolean skipOneLine() {
      if (!this.undecodedChunk.isReadable()) {
         return false;
      } else {
         byte var1 = this.undecodedChunk.readByte();
         if (var1 == 13) {
            if (!this.undecodedChunk.isReadable()) {
               this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
               return false;
            } else {
               var1 = this.undecodedChunk.readByte();
               if (var1 == 10) {
                  return true;
               } else {
                  this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
                  return false;
               }
            }
         } else if (var1 == 10) {
            return true;
         } else {
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
         }
      }
   }

   private static String[] splitMultipartHeader(String var0) {
      ArrayList var1 = new ArrayList(1);
      int var2 = HttpPostBodyUtil.findNonWhitespace(var0, 0);

      int var3;
      for (var3 = var2; var3 < var0.length(); var3++) {
         char var7 = var0.charAt(var3);
         if (var7 == ':' || Character.isWhitespace(var7)) {
            break;
         }
      }

      int var4;
      for (var4 = var3; var4 < var0.length(); var4++) {
         if (var0.charAt(var4) == ':') {
            var4++;
            break;
         }
      }

      int var5 = HttpPostBodyUtil.findNonWhitespace(var0, var4);
      int var6 = HttpPostBodyUtil.findEndOfString(var0);
      var1.add(var0.substring(var2, var3));
      String var13 = var5 >= var6 ? "" : var0.substring(var5, var6);
      String[] var8;
      if (var13.indexOf(59) >= 0) {
         var8 = splitMultipartHeaderValues(var13);
      } else {
         var8 = var13.split(",");
      }

      for (String var12 : var8) {
         var1.add(var12.trim());
      }

      String[] var14 = new String[var1.size()];

      for (int var15 = 0; var15 < var1.size(); var15++) {
         var14[var15] = (String)var1.get(var15);
      }

      return var14;
   }

   private static String[] splitMultipartHeaderValues(String var0) {
      ArrayList var1 = InternalThreadLocalMap.get().arrayList(1);
      boolean var2 = false;
      boolean var3 = false;
      int var4 = 0;

      for (int var5 = 0; var5 < var0.length(); var5++) {
         char var6 = var0.charAt(var5);
         if (var2) {
            if (var3) {
               var3 = false;
            } else if (var6 == '\\') {
               var3 = true;
            } else if (var6 == '"') {
               var2 = false;
            }
         } else if (var6 == '"') {
            var2 = true;
         } else if (var6 == ';') {
            var1.add(var0.substring(var4, var5));
            var4 = var5 + 1;
         }
      }

      var1.add(var0.substring(var4));
      return var1.toArray(new String[0]);
   }

   int getCurrentAllocatedCapacity() {
      return this.undecodedChunk.capacity();
   }
}
