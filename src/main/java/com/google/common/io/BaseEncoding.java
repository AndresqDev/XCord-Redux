package com.google.common.io;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.RoundingMode;
import java.util.Arrays;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public abstract class BaseEncoding {
   private static final BaseEncoding BASE64 = new BaseEncoding.Base64Encoding(
      "base64()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", '='
   );
   private static final BaseEncoding BASE64_URL = new BaseEncoding.Base64Encoding(
      "base64Url()", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_", '='
   );
   private static final BaseEncoding BASE32 = new BaseEncoding.StandardBaseEncoding("base32()", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", '=');
   private static final BaseEncoding BASE32_HEX = new BaseEncoding.StandardBaseEncoding("base32Hex()", "0123456789ABCDEFGHIJKLMNOPQRSTUV", '=');
   private static final BaseEncoding BASE16 = new BaseEncoding.Base16Encoding("base16()", "0123456789ABCDEF");

   BaseEncoding() {
   }

   public String encode(byte[] var1) {
      return this.encode(var1, 0, var1.length);
   }

   public final String encode(byte[] var1, int var2, int var3) {
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      StringBuilder var4 = new StringBuilder(this.maxEncodedSize(var3));

      try {
         this.encodeTo(var4, var1, var2, var3);
      } catch (IOException var6) {
         throw new AssertionError(var6);
      }

      return var4.toString();
   }

   @GwtIncompatible
   public abstract OutputStream encodingStream(Writer var1);

   @GwtIncompatible
   public final ByteSink encodingSink(final CharSink var1) {
      Preconditions.checkNotNull(var1);
      return new ByteSink() {
         @Override
         public OutputStream openStream() throws IOException {
            return BaseEncoding.this.encodingStream(var1.openStream());
         }
      };
   }

   private static byte[] extract(byte[] var0, int var1) {
      if (var1 == var0.length) {
         return var0;
      } else {
         byte[] var2 = new byte[var1];
         System.arraycopy(var0, 0, var2, 0, var1);
         return var2;
      }
   }

   public abstract boolean canDecode(CharSequence var1);

   public final byte[] decode(CharSequence var1) {
      try {
         return this.decodeChecked(var1);
      } catch (BaseEncoding.DecodingException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   final byte[] decodeChecked(CharSequence var1) throws BaseEncoding.DecodingException {
      var1 = this.trimTrailingPadding(var1);
      byte[] var2 = new byte[this.maxDecodedSize(var1.length())];
      int var3 = this.decodeTo(var2, var1);
      return extract(var2, var3);
   }

   @GwtIncompatible
   public abstract InputStream decodingStream(Reader var1);

   @GwtIncompatible
   public final ByteSource decodingSource(final CharSource var1) {
      Preconditions.checkNotNull(var1);
      return new ByteSource() {
         @Override
         public InputStream openStream() throws IOException {
            return BaseEncoding.this.decodingStream(var1.openStream());
         }
      };
   }

   abstract int maxEncodedSize(int var1);

   abstract void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException;

   abstract int maxDecodedSize(int var1);

   abstract int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException;

   CharSequence trimTrailingPadding(CharSequence var1) {
      return Preconditions.checkNotNull(var1);
   }

   public abstract BaseEncoding omitPadding();

   public abstract BaseEncoding withPadChar(char var1);

   public abstract BaseEncoding withSeparator(String var1, int var2);

   public abstract BaseEncoding upperCase();

   public abstract BaseEncoding lowerCase();

   public static BaseEncoding base64() {
      return BASE64;
   }

   public static BaseEncoding base64Url() {
      return BASE64_URL;
   }

   public static BaseEncoding base32() {
      return BASE32;
   }

   public static BaseEncoding base32Hex() {
      return BASE32_HEX;
   }

   public static BaseEncoding base16() {
      return BASE16;
   }

   @GwtIncompatible
   static Reader ignoringReader(final Reader var0, final String var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Reader() {
         @Override
         public int read() throws IOException {
            int var1x;
            do {
               var1x = var0.read();
            } while (var1x != -1 && var1.indexOf((char)var1x) >= 0);

            return var1x;
         }

         @Override
         public int read(char[] var1x, int var2, int var3) throws IOException {
            throw new UnsupportedOperationException();
         }

         @Override
         public void close() throws IOException {
            var0.close();
         }
      };
   }

   static Appendable separatingAppendable(final Appendable var0, final String var1, final int var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(var2 > 0);
      return new Appendable() {
         int charsUntilSeparator = var2;

         @Override
         public Appendable append(char var1x) throws IOException {
            if (this.charsUntilSeparator == 0) {
               var0.append(var1);
               this.charsUntilSeparator = var2;
            }

            var0.append(var1x);
            this.charsUntilSeparator--;
            return this;
         }

         @Override
         public Appendable append(CharSequence var1x, int var2x, int var3) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Appendable append(CharSequence var1x) {
            throw new UnsupportedOperationException();
         }
      };
   }

   @GwtIncompatible
   static Writer separatingWriter(final Writer var0, String var1, int var2) {
      final Appendable var3 = separatingAppendable(var0, var1, var2);
      return new Writer() {
         @Override
         public void write(int var1) throws IOException {
            var3.append((char)var1);
         }

         @Override
         public void write(char[] var1, int var2, int var3x) throws IOException {
            throw new UnsupportedOperationException();
         }

         @Override
         public void flush() throws IOException {
            var0.flush();
         }

         @Override
         public void close() throws IOException {
            var0.close();
         }
      };
   }

   private static final class Alphabet {
      private final String name;
      private final char[] chars;
      final int mask;
      final int bitsPerChar;
      final int charsPerChunk;
      final int bytesPerChunk;
      private final byte[] decodabet;
      private final boolean[] validPadding;

      Alphabet(String var1, char[] var2) {
         this.name = Preconditions.checkNotNull(var1);
         this.chars = Preconditions.checkNotNull(var2);

         try {
            this.bitsPerChar = IntMath.log2(var2.length, RoundingMode.UNNECESSARY);
         } catch (ArithmeticException var7) {
            int var4 = var2.length;
            throw new IllegalArgumentException(new StringBuilder(35).append("Illegal alphabet length ").append(var4).toString(), var7);
         }

         int var3 = Math.min(8, Integer.lowestOneBit(this.bitsPerChar));

         try {
            this.charsPerChunk = 8 / var3;
            this.bytesPerChunk = this.bitsPerChar / var3;
         } catch (ArithmeticException var8) {
            String var10003 = String.valueOf(new String(var2));
            throw new IllegalArgumentException(var10003.length() != 0 ? "Illegal alphabet ".concat(var10003) : new String("Illegal alphabet "), var8);
         }

         this.mask = var2.length - 1;
         byte[] var9 = new byte[128];
         Arrays.fill(var9, (byte)-1);

         for (int var5 = 0; var5 < var2.length; var5++) {
            char var6 = var2[var5];
            Preconditions.checkArgument(var6 < var9.length, "Non-ASCII character: %s", var6);
            Preconditions.checkArgument(var9[var6] == -1, "Duplicate character: %s", var6);
            var9[var6] = (byte)var5;
         }

         this.decodabet = var9;
         boolean[] var10 = new boolean[this.charsPerChunk];

         for (int var11 = 0; var11 < this.bytesPerChunk; var11++) {
            var10[IntMath.divide(var11 * 8, this.bitsPerChar, RoundingMode.CEILING)] = true;
         }

         this.validPadding = var10;
      }

      char encode(int var1) {
         return this.chars[var1];
      }

      boolean isValidPaddingStartPosition(int var1) {
         return this.validPadding[var1 % this.charsPerChunk];
      }

      boolean canDecode(char var1) {
         return var1 <= 127 && this.decodabet[var1] != -1;
      }

      int decode(char var1) throws BaseEncoding.DecodingException {
         if (var1 > 127) {
            String var3 = String.valueOf(Integer.toHexString(var1));
            throw new BaseEncoding.DecodingException(var3.length() != 0 ? "Unrecognized character: 0x".concat(var3) : new String("Unrecognized character: 0x"));
         } else {
            byte var2 = this.decodabet[var1];
            if (var2 == -1) {
               if (var1 > ' ' && var1 != 127) {
                  throw new BaseEncoding.DecodingException(new StringBuilder(25).append("Unrecognized character: ").append(var1).toString());
               } else {
                  String var10003 = String.valueOf(Integer.toHexString(var1));
                  throw new BaseEncoding.DecodingException(
                     var10003.length() != 0 ? "Unrecognized character: 0x".concat(var10003) : new String("Unrecognized character: 0x")
                  );
               }
            } else {
               return var2;
            }
         }
      }

      private boolean hasLowerCase() {
         for (char var4 : this.chars) {
            if (Ascii.isLowerCase(var4)) {
               return true;
            }
         }

         return false;
      }

      private boolean hasUpperCase() {
         for (char var4 : this.chars) {
            if (Ascii.isUpperCase(var4)) {
               return true;
            }
         }

         return false;
      }

      BaseEncoding.Alphabet upperCase() {
         if (!this.hasLowerCase()) {
            return this;
         } else {
            Preconditions.checkState(!this.hasUpperCase(), "Cannot call upperCase() on a mixed-case alphabet");
            char[] var1 = new char[this.chars.length];

            for (int var2 = 0; var2 < this.chars.length; var2++) {
               var1[var2] = Ascii.toUpperCase(this.chars[var2]);
            }

            return new BaseEncoding.Alphabet(String.valueOf(this.name).concat(".upperCase()"), var1);
         }
      }

      BaseEncoding.Alphabet lowerCase() {
         if (!this.hasUpperCase()) {
            return this;
         } else {
            Preconditions.checkState(!this.hasLowerCase(), "Cannot call lowerCase() on a mixed-case alphabet");
            char[] var1 = new char[this.chars.length];

            for (int var2 = 0; var2 < this.chars.length; var2++) {
               var1[var2] = Ascii.toLowerCase(this.chars[var2]);
            }

            return new BaseEncoding.Alphabet(String.valueOf(this.name).concat(".lowerCase()"), var1);
         }
      }

      public boolean matches(char var1) {
         return var1 < this.decodabet.length && this.decodabet[var1] != -1;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof BaseEncoding.Alphabet) {
            BaseEncoding.Alphabet var2 = (BaseEncoding.Alphabet)var1;
            return Arrays.equals(this.chars, var2.chars);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode(this.chars);
      }
   }

   static final class Base16Encoding extends BaseEncoding.StandardBaseEncoding {
      final char[] encoding = new char[512];

      Base16Encoding(String var1, String var2) {
         this(new BaseEncoding.Alphabet(var1, var2.toCharArray()));
      }

      private Base16Encoding(BaseEncoding.Alphabet var1) {
         super(var1, null);
         Preconditions.checkArgument(var1.chars.length == 16);

         for (int var2 = 0; var2 < 256; var2++) {
            this.encoding[var2] = var1.encode(var2 >>> 4);
            this.encoding[var2 | 256] = var1.encode(var2 & 15);
         }
      }

      @Override
      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);

         for (int var5 = 0; var5 < var4; var5++) {
            int var6 = var2[var3 + var5] & 255;
            var1.append(this.encoding[var6]);
            var1.append(this.encoding[var6 | 256]);
         }
      }

      @Override
      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         Preconditions.checkNotNull(var1);
         if (var2.length() % 2 == 1) {
            int var6 = var2.length();
            throw new BaseEncoding.DecodingException(new StringBuilder(32).append("Invalid input length ").append(var6).toString());
         } else {
            int var3 = 0;

            for (byte var4 = 0; var4 < var2.length(); var4 += 2) {
               int var5 = this.alphabet.decode(var2.charAt(var4)) << 4 | this.alphabet.decode(var2.charAt(var4 + 1));
               var1[var3++] = (byte)var5;
            }

            return var3;
         }
      }

      @Override
      BaseEncoding newInstance(BaseEncoding.Alphabet var1, Character var2) {
         return new BaseEncoding.Base16Encoding(var1);
      }
   }

   static final class Base64Encoding extends BaseEncoding.StandardBaseEncoding {
      Base64Encoding(String var1, String var2, Character var3) {
         this(new BaseEncoding.Alphabet(var1, var2.toCharArray()), var3);
      }

      private Base64Encoding(BaseEncoding.Alphabet var1, Character var2) {
         super(var1, var2);
         Preconditions.checkArgument(var1.chars.length == 64);
      }

      @Override
      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);
         int var5 = var3;

         for (int var6 = var4; var6 >= 3; var6 -= 3) {
            int var7 = (var2[var5++] & 255) << 16 | (var2[var5++] & 255) << 8 | var2[var5++] & 255;
            var1.append(this.alphabet.encode(var7 >>> 18));
            var1.append(this.alphabet.encode(var7 >>> 12 & 63));
            var1.append(this.alphabet.encode(var7 >>> 6 & 63));
            var1.append(this.alphabet.encode(var7 & 63));
         }

         if (var5 < var3 + var4) {
            this.encodeChunkTo(var1, var2, var5, var3 + var4 - var5);
         }
      }

      @Override
      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         Preconditions.checkNotNull(var1);
         var2 = this.trimTrailingPadding(var2);
         if (!this.alphabet.isValidPaddingStartPosition(var2.length())) {
            int var7 = var2.length();
            throw new BaseEncoding.DecodingException(new StringBuilder(32).append("Invalid input length ").append(var7).toString());
         } else {
            int var3 = 0;
            int var4 = 0;

            while (var4 < var2.length()) {
               int var5 = this.alphabet.decode(var2.charAt(var4++)) << 18;
               var5 |= this.alphabet.decode(var2.charAt(var4++)) << 12;
               var1[var3++] = (byte)(var5 >>> 16);
               if (var4 < var2.length()) {
                  var5 |= this.alphabet.decode(var2.charAt(var4++)) << 6;
                  var1[var3++] = (byte)(var5 >>> 8 & 0xFF);
                  if (var4 < var2.length()) {
                     var5 |= this.alphabet.decode(var2.charAt(var4++));
                     var1[var3++] = (byte)(var5 & 0xFF);
                  }
               }
            }

            return var3;
         }
      }

      @Override
      BaseEncoding newInstance(BaseEncoding.Alphabet var1, Character var2) {
         return new BaseEncoding.Base64Encoding(var1, var2);
      }
   }

   public static final class DecodingException extends IOException {
      DecodingException(String var1) {
         super(var1);
      }

      DecodingException(Throwable var1) {
         super(var1);
      }
   }

   static final class SeparatedBaseEncoding extends BaseEncoding {
      private final BaseEncoding delegate;
      private final String separator;
      private final int afterEveryChars;

      SeparatedBaseEncoding(BaseEncoding var1, String var2, int var3) {
         this.delegate = Preconditions.checkNotNull(var1);
         this.separator = Preconditions.checkNotNull(var2);
         this.afterEveryChars = var3;
         Preconditions.checkArgument(var3 > 0, "Cannot add a separator after every %s chars", var3);
      }

      @Override
      CharSequence trimTrailingPadding(CharSequence var1) {
         return this.delegate.trimTrailingPadding(var1);
      }

      @Override
      int maxEncodedSize(int var1) {
         int var2 = this.delegate.maxEncodedSize(var1);
         return var2 + this.separator.length() * IntMath.divide(Math.max(0, var2 - 1), this.afterEveryChars, RoundingMode.FLOOR);
      }

      @GwtIncompatible
      @Override
      public OutputStream encodingStream(Writer var1) {
         return this.delegate.encodingStream(separatingWriter(var1, this.separator, this.afterEveryChars));
      }

      @Override
      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         this.delegate.encodeTo(separatingAppendable(var1, this.separator, this.afterEveryChars), var2, var3, var4);
      }

      @Override
      int maxDecodedSize(int var1) {
         return this.delegate.maxDecodedSize(var1);
      }

      @Override
      public boolean canDecode(CharSequence var1) {
         StringBuilder var2 = new StringBuilder();

         for (int var3 = 0; var3 < var1.length(); var3++) {
            char var4 = var1.charAt(var3);
            if (this.separator.indexOf(var4) < 0) {
               var2.append(var4);
            }
         }

         return this.delegate.canDecode(var2);
      }

      @Override
      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         StringBuilder var3 = new StringBuilder(var2.length());

         for (int var4 = 0; var4 < var2.length(); var4++) {
            char var5 = var2.charAt(var4);
            if (this.separator.indexOf(var5) < 0) {
               var3.append(var5);
            }
         }

         return this.delegate.decodeTo(var1, var3);
      }

      @GwtIncompatible
      @Override
      public InputStream decodingStream(Reader var1) {
         return this.delegate.decodingStream(ignoringReader(var1, this.separator));
      }

      @Override
      public BaseEncoding omitPadding() {
         return this.delegate.omitPadding().withSeparator(this.separator, this.afterEveryChars);
      }

      @Override
      public BaseEncoding withPadChar(char var1) {
         return this.delegate.withPadChar(var1).withSeparator(this.separator, this.afterEveryChars);
      }

      @Override
      public BaseEncoding withSeparator(String var1, int var2) {
         throw new UnsupportedOperationException("Already have a separator");
      }

      @Override
      public BaseEncoding upperCase() {
         return this.delegate.upperCase().withSeparator(this.separator, this.afterEveryChars);
      }

      @Override
      public BaseEncoding lowerCase() {
         return this.delegate.lowerCase().withSeparator(this.separator, this.afterEveryChars);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.delegate);
         String var2 = this.separator;
         int var3 = this.afterEveryChars;
         return new StringBuilder(31 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".withSeparator(\"")
            .append(var2)
            .append("\", ")
            .append(var3)
            .append(")")
            .toString();
      }
   }

   static class StandardBaseEncoding extends BaseEncoding {
      final BaseEncoding.Alphabet alphabet;
      final Character paddingChar;
      @LazyInit
      private transient BaseEncoding upperCase;
      @LazyInit
      private transient BaseEncoding lowerCase;

      StandardBaseEncoding(String var1, String var2, Character var3) {
         this(new BaseEncoding.Alphabet(var1, var2.toCharArray()), var3);
      }

      StandardBaseEncoding(BaseEncoding.Alphabet var1, Character var2) {
         this.alphabet = Preconditions.checkNotNull(var1);
         Preconditions.checkArgument(var2 == null || !var1.matches(var2), "Padding character %s was already in alphabet", var2);
         this.paddingChar = var2;
      }

      @Override
      int maxEncodedSize(int var1) {
         return this.alphabet.charsPerChunk * IntMath.divide(var1, this.alphabet.bytesPerChunk, RoundingMode.CEILING);
      }

      @GwtIncompatible
      @Override
      public OutputStream encodingStream(final Writer var1) {
         Preconditions.checkNotNull(var1);
         return new OutputStream() {
            int bitBuffer = 0;
            int bitBufferLength = 0;
            int writtenChars = 0;

            @Override
            public void write(int var1x) throws IOException {
               this.bitBuffer <<= 8;
               this.bitBuffer |= var1x & 0xFF;

               for (this.bitBufferLength += 8;
                  this.bitBufferLength >= StandardBaseEncoding.this.alphabet.bitsPerChar;
                  this.bitBufferLength = this.bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar
               ) {
                  int var2 = this.bitBuffer >> this.bitBufferLength - StandardBaseEncoding.this.alphabet.bitsPerChar & StandardBaseEncoding.this.alphabet.mask;
                  var1.write(StandardBaseEncoding.this.alphabet.encode(var2));
                  this.writtenChars++;
               }
            }

            @Override
            public void flush() throws IOException {
               var1.flush();
            }

            @Override
            public void close() throws IOException {
               if (this.bitBufferLength > 0) {
                  int var1x = this.bitBuffer << StandardBaseEncoding.this.alphabet.bitsPerChar - this.bitBufferLength & StandardBaseEncoding.this.alphabet.mask;
                  var1.write(StandardBaseEncoding.this.alphabet.encode(var1x));
                  this.writtenChars++;
                  if (StandardBaseEncoding.this.paddingChar != null) {
                     while (this.writtenChars % StandardBaseEncoding.this.alphabet.charsPerChunk != 0) {
                        var1.write(StandardBaseEncoding.this.paddingChar);
                        this.writtenChars++;
                     }
                  }
               }

               var1.close();
            }
         };
      }

      @Override
      void encodeTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);
         int var5 = 0;

         while (var5 < var4) {
            this.encodeChunkTo(var1, var2, var3 + var5, Math.min(this.alphabet.bytesPerChunk, var4 - var5));
            var5 += this.alphabet.bytesPerChunk;
         }
      }

      void encodeChunkTo(Appendable var1, byte[] var2, int var3, int var4) throws IOException {
         Preconditions.checkNotNull(var1);
         Preconditions.checkPositionIndexes(var3, var3 + var4, var2.length);
         Preconditions.checkArgument(var4 <= this.alphabet.bytesPerChunk);
         long var5 = 0L;

         for (int var7 = 0; var7 < var4; var7++) {
            var5 |= (long)(var2[var3 + var7] & 255);
            var5 <<= 8;
         }

         int var11 = (var4 + 1) * 8 - this.alphabet.bitsPerChar;

         int var8;
         for (var8 = 0; var8 < var4 * 8; var8 += this.alphabet.bitsPerChar) {
            int var9 = (int)(var5 >>> var11 - var8) & this.alphabet.mask;
            var1.append(this.alphabet.encode(var9));
         }

         if (this.paddingChar != null) {
            while (var8 < this.alphabet.bytesPerChunk * 8) {
               var1.append(this.paddingChar);
               var8 += this.alphabet.bitsPerChar;
            }
         }
      }

      @Override
      int maxDecodedSize(int var1) {
         return (int)(((long)this.alphabet.bitsPerChar * (long)var1 + 7L) / 8L);
      }

      @Override
      CharSequence trimTrailingPadding(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         if (this.paddingChar == null) {
            return var1;
         } else {
            char var2 = this.paddingChar;
            int var3 = var1.length() - 1;

            while (var3 >= 0 && var1.charAt(var3) == var2) {
               var3--;
            }

            return var1.subSequence(0, var3 + 1);
         }
      }

      @Override
      public boolean canDecode(CharSequence var1) {
         Preconditions.checkNotNull(var1);
         var1 = this.trimTrailingPadding(var1);
         if (!this.alphabet.isValidPaddingStartPosition(var1.length())) {
            return false;
         } else {
            for (int var2 = 0; var2 < var1.length(); var2++) {
               if (!this.alphabet.canDecode(var1.charAt(var2))) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      int decodeTo(byte[] var1, CharSequence var2) throws BaseEncoding.DecodingException {
         Preconditions.checkNotNull(var1);
         var2 = this.trimTrailingPadding(var2);
         if (!this.alphabet.isValidPaddingStartPosition(var2.length())) {
            int var11 = var2.length();
            throw new BaseEncoding.DecodingException(new StringBuilder(32).append("Invalid input length ").append(var11).toString());
         } else {
            int var3 = 0;

            for (int var4 = 0; var4 < var2.length(); var4 += this.alphabet.charsPerChunk) {
               long var5 = 0L;
               int var7 = 0;

               for (int var8 = 0; var8 < this.alphabet.charsPerChunk; var8++) {
                  var5 <<= this.alphabet.bitsPerChar;
                  if (var4 + var8 < var2.length()) {
                     var5 |= (long)this.alphabet.decode(var2.charAt(var4 + var7++));
                  }
               }

               int var12 = this.alphabet.bytesPerChunk * 8 - var7 * this.alphabet.bitsPerChar;

               for (int var9 = (this.alphabet.bytesPerChunk - 1) * 8; var9 >= var12; var9 -= 8) {
                  var1[var3++] = (byte)((int)(var5 >>> var9 & 255L));
               }
            }

            return var3;
         }
      }

      @GwtIncompatible
      @Override
      public InputStream decodingStream(final Reader var1) {
         Preconditions.checkNotNull(var1);
         return new InputStream() {
            int bitBuffer = 0;
            int bitBufferLength = 0;
            int readChars = 0;
            boolean hitPadding = false;

            @Override
            public int read() throws IOException {
               while (true) {
                  int var1x = var1.read();
                  if (var1x == -1) {
                     if (!this.hitPadding && !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars)) {
                        int var4 = this.readChars;
                        throw new BaseEncoding.DecodingException(new StringBuilder(32).append("Invalid input length ").append(var4).toString());
                     }

                     return -1;
                  }

                  this.readChars++;
                  char var2 = (char)var1x;
                  if (StandardBaseEncoding.this.paddingChar != null && StandardBaseEncoding.this.paddingChar == var2) {
                     if (!this.hitPadding && (this.readChars == 1 || !StandardBaseEncoding.this.alphabet.isValidPaddingStartPosition(this.readChars - 1))) {
                        int var5 = this.readChars;
                        throw new BaseEncoding.DecodingException(new StringBuilder(41).append("Padding cannot start at index ").append(var5).toString());
                     }

                     this.hitPadding = true;
                  } else {
                     if (this.hitPadding) {
                        int var3 = this.readChars;
                        throw new BaseEncoding.DecodingException(
                           new StringBuilder(61).append("Expected padding character but found '").append(var2).append("' at index ").append(var3).toString()
                        );
                     }

                     this.bitBuffer = this.bitBuffer << StandardBaseEncoding.this.alphabet.bitsPerChar;
                     this.bitBuffer = this.bitBuffer | StandardBaseEncoding.this.alphabet.decode(var2);
                     this.bitBufferLength = this.bitBufferLength + StandardBaseEncoding.this.alphabet.bitsPerChar;
                     if (this.bitBufferLength >= 8) {
                        this.bitBufferLength -= 8;
                        return this.bitBuffer >> this.bitBufferLength & 0xFF;
                     }
                  }
               }
            }

            @Override
            public int read(byte[] var1x, int var2, int var3) throws IOException {
               Preconditions.checkPositionIndexes(var2, var2 + var3, var1x.length);

               int var4;
               for (var4 = var2; var4 < var2 + var3; var4++) {
                  int var5 = this.read();
                  if (var5 == -1) {
                     int var6 = var4 - var2;
                     return var6 == 0 ? -1 : var6;
                  }

                  var1x[var4] = (byte)var5;
               }

               return var4 - var2;
            }

            @Override
            public void close() throws IOException {
               var1.close();
            }
         };
      }

      @Override
      public BaseEncoding omitPadding() {
         return (BaseEncoding)(this.paddingChar == null ? this : this.newInstance(this.alphabet, null));
      }

      @Override
      public BaseEncoding withPadChar(char var1) {
         return (BaseEncoding)(8 % this.alphabet.bitsPerChar != 0 && (this.paddingChar == null || this.paddingChar != var1)
            ? this.newInstance(this.alphabet, var1)
            : this);
      }

      @Override
      public BaseEncoding withSeparator(String var1, int var2) {
         for (int var3 = 0; var3 < var1.length(); var3++) {
            Preconditions.checkArgument(!this.alphabet.matches(var1.charAt(var3)), "Separator (%s) cannot contain alphabet characters", var1);
         }

         if (this.paddingChar != null) {
            Preconditions.checkArgument(var1.indexOf(this.paddingChar) < 0, "Separator (%s) cannot contain padding character", var1);
         }

         return new BaseEncoding.SeparatedBaseEncoding(this, var1, var2);
      }

      @Override
      public BaseEncoding upperCase() {
         Object var1 = this.upperCase;
         if (var1 == null) {
            BaseEncoding.Alphabet var2 = this.alphabet.upperCase();
            var1 = this.upperCase = (BaseEncoding)(var2 == this.alphabet ? this : this.newInstance(var2, this.paddingChar));
         }

         return (BaseEncoding)var1;
      }

      @Override
      public BaseEncoding lowerCase() {
         Object var1 = this.lowerCase;
         if (var1 == null) {
            BaseEncoding.Alphabet var2 = this.alphabet.lowerCase();
            var1 = this.lowerCase = (BaseEncoding)(var2 == this.alphabet ? this : this.newInstance(var2, this.paddingChar));
         }

         return (BaseEncoding)var1;
      }

      BaseEncoding newInstance(BaseEncoding.Alphabet var1, Character var2) {
         return new BaseEncoding.StandardBaseEncoding(var1, var2);
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder("BaseEncoding.");
         var1.append(this.alphabet.toString());
         if (8 % this.alphabet.bitsPerChar != 0) {
            if (this.paddingChar == null) {
               var1.append(".omitPadding()");
            } else {
               var1.append(".withPadChar('").append(this.paddingChar).append("')");
            }
         }

         return var1.toString();
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof BaseEncoding.StandardBaseEncoding)) {
            return false;
         } else {
            BaseEncoding.StandardBaseEncoding var2 = (BaseEncoding.StandardBaseEncoding)var1;
            return this.alphabet.equals(var2.alphabet) && Objects.equal(this.paddingChar, var2.paddingChar);
         }
      }

      @Override
      public int hashCode() {
         return this.alphabet.hashCode() ^ Objects.hashCode(this.paddingChar);
      }
   }
}
