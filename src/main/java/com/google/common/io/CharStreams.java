package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class CharStreams {
   private static final int DEFAULT_BUF_SIZE = 2048;

   static CharBuffer createBuffer() {
      return CharBuffer.allocate(2048);
   }

   private CharStreams() {
   }

   @CanIgnoreReturnValue
   public static long copy(Readable var0, Appendable var1) throws IOException {
      if (var0 instanceof Reader) {
         return var1 instanceof StringBuilder ? copyReaderToBuilder((Reader)var0, (StringBuilder)var1) : copyReaderToWriter((Reader)var0, asWriter(var1));
      } else {
         Preconditions.checkNotNull(var0);
         Preconditions.checkNotNull(var1);
         long var2 = 0L;
         CharBuffer var4 = createBuffer();

         while (var0.read(var4) != -1) {
            Java8Compatibility.flip(var4);
            var1.append(var4);
            var2 += (long)var4.remaining();
            Java8Compatibility.clear(var4);
         }

         return var2;
      }
   }

   @CanIgnoreReturnValue
   static long copyReaderToBuilder(Reader var0, StringBuilder var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      char[] var2 = new char[2048];
      long var4 = 0L;

      int var3;
      while ((var3 = var0.read(var2)) != -1) {
         var1.append(var2, 0, var3);
         var4 += (long)var3;
      }

      return var4;
   }

   @CanIgnoreReturnValue
   static long copyReaderToWriter(Reader var0, Writer var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      char[] var2 = new char[2048];
      long var4 = 0L;

      int var3;
      while ((var3 = var0.read(var2)) != -1) {
         var1.write(var2, 0, var3);
         var4 += (long)var3;
      }

      return var4;
   }

   public static String toString(Readable var0) throws IOException {
      return toStringBuilder(var0).toString();
   }

   private static StringBuilder toStringBuilder(Readable var0) throws IOException {
      StringBuilder var1 = new StringBuilder();
      if (var0 instanceof Reader) {
         copyReaderToBuilder((Reader)var0, var1);
      } else {
         copy(var0, var1);
      }

      return var1;
   }

   @Beta
   public static List<String> readLines(Readable var0) throws IOException {
      ArrayList var1 = new ArrayList();
      LineReader var2 = new LineReader(var0);

      String var3;
      while ((var3 = var2.readLine()) != null) {
         var1.add(var3);
      }

      return var1;
   }

   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   public static <T> T readLines(Readable var0, LineProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      LineReader var2 = new LineReader(var0);

      String var3;
      while ((var3 = var2.readLine()) != null && var1.processLine(var3)) {
      }

      return (T)var1.getResult();
   }

   @Beta
   @CanIgnoreReturnValue
   public static long exhaust(Readable var0) throws IOException {
      long var1 = 0L;
      CharBuffer var5 = createBuffer();

      long var3;
      while ((var3 = (long)var0.read(var5)) != -1L) {
         var1 += var3;
         Java8Compatibility.clear(var5);
      }

      return var1;
   }

   @Beta
   public static void skipFully(Reader var0, long var1) throws IOException {
      Preconditions.checkNotNull(var0);

      while (var1 > 0L) {
         long var3 = var0.skip(var1);
         if (var3 == 0L) {
            throw new EOFException();
         }

         var1 -= var3;
      }
   }

   @Beta
   public static Writer nullWriter() {
      return CharStreams.NullWriter.INSTANCE;
   }

   @Beta
   public static Writer asWriter(Appendable var0) {
      return (Writer)(var0 instanceof Writer ? (Writer)var0 : new AppendableWriter(var0));
   }

   private static final class NullWriter extends Writer {
      private static final CharStreams.NullWriter INSTANCE = new CharStreams.NullWriter();

      @Override
      public void write(int var1) {
      }

      @Override
      public void write(char[] var1) {
         Preconditions.checkNotNull(var1);
      }

      @Override
      public void write(char[] var1, int var2, int var3) {
         Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      }

      @Override
      public void write(String var1) {
         Preconditions.checkNotNull(var1);
      }

      @Override
      public void write(String var1, int var2, int var3) {
         Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length());
      }

      @Override
      public Writer append(CharSequence var1) {
         return this;
      }

      @Override
      public Writer append(CharSequence var1, int var2, int var3) {
         Preconditions.checkPositionIndexes(var2, var3, var1 == null ? "null".length() : var1.length());
         return this;
      }

      @Override
      public Writer append(char var1) {
         return this;
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() {
      }

      @Override
      public String toString() {
         return "CharStreams.nullWriter()";
      }
   }
}
