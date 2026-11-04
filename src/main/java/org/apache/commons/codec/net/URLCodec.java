package org.apache.commons.codec.net;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;

public class URLCodec implements BinaryDecoder, BinaryEncoder, StringDecoder, StringEncoder {
   @Deprecated
   protected volatile String charset;
   protected static final byte ESCAPE_CHAR = 37;
   @Deprecated
   protected static final BitSet WWW_FORM_URL;
   private static final BitSet WWW_FORM_URL_SAFE = new BitSet(256);

   public URLCodec() {
      this("UTF-8");
   }

   public URLCodec(String var1) {
      this.charset = var1;
   }

   public static final byte[] encodeUrl(BitSet var0, byte[] var1) {
      if (var1 == null) {
         return null;
      } else {
         if (var0 == null) {
            var0 = WWW_FORM_URL_SAFE;
         }

         ByteArrayOutputStream var2 = new ByteArrayOutputStream();

         for (byte var6 : var1) {
            int var7 = var6;
            if (var6 < 0) {
               var7 = 256 + var6;
            }

            if (var0.get(var7)) {
               if (var7 == 32) {
                  var7 = 43;
               }

               var2.write(var7);
            } else {
               var2.write(37);
               char var8 = Utils.hexDigit(var7 >> 4);
               char var9 = Utils.hexDigit(var7);
               var2.write(var8);
               var2.write(var9);
            }
         }

         return var2.toByteArray();
      }
   }

   public static final byte[] decodeUrl(byte[] var0) throws DecoderException {
      if (var0 == null) {
         return null;
      } else {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();

         for (int var2 = 0; var2 < var0.length; var2++) {
            byte var3 = var0[var2];
            if (var3 == 43) {
               var1.write(32);
            } else if (var3 == 37) {
               try {
                  int var4 = Utils.digit16(var0[++var2]);
                  int var5 = Utils.digit16(var0[++var2]);
                  var1.write((char)((var4 << 4) + var5));
               } catch (ArrayIndexOutOfBoundsException var6) {
                  throw new DecoderException("Invalid URL encoding: ", var6);
               }
            } else {
               var1.write(var3);
            }
         }

         return var1.toByteArray();
      }
   }

   @Override
   public byte[] encode(byte[] var1) {
      return encodeUrl(WWW_FORM_URL_SAFE, var1);
   }

   @Override
   public byte[] decode(byte[] var1) throws DecoderException {
      return decodeUrl(var1);
   }

   public String encode(String var1, String var2) throws UnsupportedEncodingException {
      return var1 == null ? null : StringUtils.newStringUsAscii(this.encode(var1.getBytes(var2)));
   }

   @Override
   public String encode(String var1) throws EncoderException {
      if (var1 == null) {
         return null;
      } else {
         try {
            return this.encode(var1, this.getDefaultCharset());
         } catch (UnsupportedEncodingException var3) {
            throw new EncoderException(var3.getMessage(), var3);
         }
      }
   }

   public String decode(String var1, String var2) throws DecoderException, UnsupportedEncodingException {
      return var1 == null ? null : new String(this.decode(StringUtils.getBytesUsAscii(var1)), var2);
   }

   @Override
   public String decode(String var1) throws DecoderException {
      if (var1 == null) {
         return null;
      } else {
         try {
            return this.decode(var1, this.getDefaultCharset());
         } catch (UnsupportedEncodingException var3) {
            throw new DecoderException(var3.getMessage(), var3);
         }
      }
   }

   @Override
   public Object encode(Object var1) throws EncoderException {
      if (var1 == null) {
         return null;
      } else if (var1 instanceof byte[]) {
         return this.encode((byte[])var1);
      } else if (var1 instanceof String) {
         return this.encode((String)var1);
      } else {
         throw new EncoderException("Objects of type " + var1.getClass().getName() + " cannot be URL encoded");
      }
   }

   @Override
   public Object decode(Object var1) throws DecoderException {
      if (var1 == null) {
         return null;
      } else if (var1 instanceof byte[]) {
         return this.decode((byte[])var1);
      } else if (var1 instanceof String) {
         return this.decode((String)var1);
      } else {
         throw new DecoderException("Objects of type " + var1.getClass().getName() + " cannot be URL decoded");
      }
   }

   public String getDefaultCharset() {
      return this.charset;
   }

   @Deprecated
   public String getEncoding() {
      return this.charset;
   }

   static {
      for (int var0 = 97; var0 <= 122; var0++) {
         WWW_FORM_URL_SAFE.set(var0);
      }

      for (int var1 = 65; var1 <= 90; var1++) {
         WWW_FORM_URL_SAFE.set(var1);
      }

      for (int var2 = 48; var2 <= 57; var2++) {
         WWW_FORM_URL_SAFE.set(var2);
      }

      WWW_FORM_URL_SAFE.set(45);
      WWW_FORM_URL_SAFE.set(95);
      WWW_FORM_URL_SAFE.set(46);
      WWW_FORM_URL_SAFE.set(42);
      WWW_FORM_URL_SAFE.set(32);
      WWW_FORM_URL = (BitSet)WWW_FORM_URL_SAFE.clone();
   }
}
