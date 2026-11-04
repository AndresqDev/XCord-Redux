package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.BitSet;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

public class QCodec extends RFC1522Codec implements StringDecoder, StringEncoder {
   private final Charset charset;
   private static final BitSet PRINTABLE_CHARS = new BitSet(256);
   private static final byte BLANK = 32;
   private static final byte UNDERSCORE = 95;
   private boolean encodeBlanks = false;

   public QCodec() {
      this(Charsets.UTF_8);
   }

   public QCodec(Charset var1) {
      this.charset = var1;
   }

   public QCodec(String var1) {
      this(Charset.forName(var1));
   }

   @Override
   protected String getEncoding() {
      return "Q";
   }

   @Override
   protected byte[] doEncoding(byte[] var1) {
      if (var1 == null) {
         return null;
      } else {
         byte[] var2 = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, var1);
         if (this.encodeBlanks) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               if (var2[var3] == 32) {
                  var2[var3] = 95;
               }
            }
         }

         return var2;
      }
   }

   @Override
   protected byte[] doDecoding(byte[] var1) throws DecoderException {
      if (var1 == null) {
         return null;
      } else {
         boolean var2 = false;

         for (byte var6 : var1) {
            if (var6 == 95) {
               var2 = true;
               break;
            }
         }

         if (var2) {
            byte[] var7 = new byte[var1.length];

            for (int var8 = 0; var8 < var1.length; var8++) {
               byte var9 = var1[var8];
               if (var9 != 95) {
                  var7[var8] = var9;
               } else {
                  var7[var8] = 32;
               }
            }

            return QuotedPrintableCodec.decodeQuotedPrintable(var7);
         } else {
            return QuotedPrintableCodec.decodeQuotedPrintable(var1);
         }
      }
   }

   public String encode(String var1, Charset var2) throws EncoderException {
      return var1 == null ? null : this.encodeText(var1, var2);
   }

   public String encode(String var1, String var2) throws EncoderException {
      if (var1 == null) {
         return null;
      } else {
         try {
            return this.encodeText(var1, var2);
         } catch (UnsupportedEncodingException var4) {
            throw new EncoderException(var4.getMessage(), var4);
         }
      }
   }

   @Override
   public String encode(String var1) throws EncoderException {
      return var1 == null ? null : this.encode(var1, this.getCharset());
   }

   @Override
   public String decode(String var1) throws DecoderException {
      if (var1 == null) {
         return null;
      } else {
         try {
            return this.decodeText(var1);
         } catch (UnsupportedEncodingException var3) {
            throw new DecoderException(var3.getMessage(), var3);
         }
      }
   }

   @Override
   public Object encode(Object var1) throws EncoderException {
      if (var1 == null) {
         return null;
      } else if (var1 instanceof String) {
         return this.encode((String)var1);
      } else {
         throw new EncoderException("Objects of type " + var1.getClass().getName() + " cannot be encoded using Q codec");
      }
   }

   @Override
   public Object decode(Object var1) throws DecoderException {
      if (var1 == null) {
         return null;
      } else if (var1 instanceof String) {
         return this.decode((String)var1);
      } else {
         throw new DecoderException("Objects of type " + var1.getClass().getName() + " cannot be decoded using Q codec");
      }
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getDefaultCharset() {
      return this.charset.name();
   }

   public boolean isEncodeBlanks() {
      return this.encodeBlanks;
   }

   public void setEncodeBlanks(boolean var1) {
      this.encodeBlanks = var1;
   }

   static {
      PRINTABLE_CHARS.set(32);
      PRINTABLE_CHARS.set(33);
      PRINTABLE_CHARS.set(34);
      PRINTABLE_CHARS.set(35);
      PRINTABLE_CHARS.set(36);
      PRINTABLE_CHARS.set(37);
      PRINTABLE_CHARS.set(38);
      PRINTABLE_CHARS.set(39);
      PRINTABLE_CHARS.set(40);
      PRINTABLE_CHARS.set(41);
      PRINTABLE_CHARS.set(42);
      PRINTABLE_CHARS.set(43);
      PRINTABLE_CHARS.set(44);
      PRINTABLE_CHARS.set(45);
      PRINTABLE_CHARS.set(46);
      PRINTABLE_CHARS.set(47);

      for (int var0 = 48; var0 <= 57; var0++) {
         PRINTABLE_CHARS.set(var0);
      }

      PRINTABLE_CHARS.set(58);
      PRINTABLE_CHARS.set(59);
      PRINTABLE_CHARS.set(60);
      PRINTABLE_CHARS.set(62);
      PRINTABLE_CHARS.set(64);

      for (int var1 = 65; var1 <= 90; var1++) {
         PRINTABLE_CHARS.set(var1);
      }

      PRINTABLE_CHARS.set(91);
      PRINTABLE_CHARS.set(92);
      PRINTABLE_CHARS.set(93);
      PRINTABLE_CHARS.set(94);
      PRINTABLE_CHARS.set(96);

      for (int var2 = 97; var2 <= 122; var2++) {
         PRINTABLE_CHARS.set(var2);
      }

      PRINTABLE_CHARS.set(123);
      PRINTABLE_CHARS.set(124);
      PRINTABLE_CHARS.set(125);
      PRINTABLE_CHARS.set(126);
   }
}
