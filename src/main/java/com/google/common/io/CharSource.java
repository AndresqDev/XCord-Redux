package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.MustBeClosed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class CharSource {
   protected CharSource() {
   }

   @Beta
   public ByteSource asByteSource(Charset var1) {
      return new CharSource.AsByteSource(var1);
   }

   public abstract Reader openStream() throws IOException;

   public BufferedReader openBufferedStream() throws IOException {
      Reader var1 = this.openStream();
      return var1 instanceof BufferedReader ? (BufferedReader)var1 : new BufferedReader(var1);
   }

   @Beta
   @MustBeClosed
   public Stream<String> lines() throws IOException {
      BufferedReader var1 = this.openBufferedStream();
      return var1.lines().onClose(() -> {
         try {
            var1.close();
         } catch (IOException var2) {
            throw new UncheckedIOException(var2);
         }
      });
   }

   @Beta
   public Optional<Long> lengthIfKnown() {
      return Optional.absent();
   }

   @Beta
   public long length() throws IOException {
      Optional var1 = this.lengthIfKnown();
      if (var1.isPresent()) {
         return (Long)var1.get();
      } else {
         Closer var2 = Closer.create();

         long var4;
         try {
            Reader var3 = var2.register(this.openStream());
            var4 = this.countBySkipping(var3);
         } catch (Throwable var9) {
            throw var2.rethrow(var9);
         } finally {
            var2.close();
         }

         return var4;
      }
   }

   private long countBySkipping(Reader var1) throws IOException {
      long var2 = 0L;

      long var4;
      while ((var4 = var1.skip(Long.MAX_VALUE)) != 0L) {
         var2 += var4;
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public long copyTo(Appendable var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var4;
      try {
         Reader var3 = var2.register(this.openStream());
         var4 = CharStreams.copy(var3, var1);
      } catch (Throwable var9) {
         throw var2.rethrow(var9);
      } finally {
         var2.close();
      }

      return var4;
   }

   @CanIgnoreReturnValue
   public long copyTo(CharSink var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var5;
      try {
         Reader var3 = var2.register(this.openStream());
         Writer var4 = var2.register(var1.openStream());
         var5 = CharStreams.copy(var3, var4);
      } catch (Throwable var10) {
         throw var2.rethrow(var10);
      } finally {
         var2.close();
      }

      return var5;
   }

   public String read() throws IOException {
      Closer var1 = Closer.create();

      String var3;
      try {
         Reader var2 = var1.register(this.openStream());
         var3 = CharStreams.toString(var2);
      } catch (Throwable var7) {
         throw var1.rethrow(var7);
      } finally {
         var1.close();
      }

      return var3;
   }

   public String readFirstLine() throws IOException {
      Closer var1 = Closer.create();

      String var3;
      try {
         BufferedReader var2 = var1.register(this.openBufferedStream());
         var3 = var2.readLine();
      } catch (Throwable var7) {
         throw var1.rethrow(var7);
      } finally {
         var1.close();
      }

      return var3;
   }

   public ImmutableList<String> readLines() throws IOException {
      Closer var1 = Closer.create();

      ImmutableList var5;
      try {
         BufferedReader var2 = var1.register(this.openBufferedStream());
         ArrayList var3 = Lists.newArrayList();

         String var4;
         while ((var4 = var2.readLine()) != null) {
            var3.add(var4);
         }

         var5 = ImmutableList.copyOf(var3);
      } catch (Throwable var9) {
         throw var1.rethrow(var9);
      } finally {
         var1.close();
      }

      return var5;
   }

   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   public <T> T readLines(LineProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      Object var4;
      try {
         Reader var3 = var2.register(this.openStream());
         var4 = CharStreams.readLines(var3, var1);
      } catch (Throwable var8) {
         throw var2.rethrow(var8);
      } finally {
         var2.close();
      }

      return (T)var4;
   }

   @Beta
   public void forEachLine(Consumer<? super String> var1) throws IOException {
      try {
         Stream var2 = this.lines();

         try {
            var2.forEachOrdered(var1);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (UncheckedIOException var7) {
         throw var7.getCause();
      }
   }

   public boolean isEmpty() throws IOException {
      Optional var1 = this.lengthIfKnown();
      if (var1.isPresent()) {
         return (Long)var1.get() == 0L;
      } else {
         Closer var2 = Closer.create();

         boolean var4;
         try {
            Reader var3 = var2.register(this.openStream());
            var4 = var3.read() == -1;
         } catch (Throwable var8) {
            throw var2.rethrow(var8);
         } finally {
            var2.close();
         }

         return var4;
      }
   }

   public static CharSource concat(Iterable<? extends CharSource> var0) {
      return new CharSource.ConcatenatedCharSource(var0);
   }

   public static CharSource concat(Iterator<? extends CharSource> var0) {
      return concat(ImmutableList.copyOf(var0));
   }

   public static CharSource concat(CharSource... var0) {
      return concat(ImmutableList.copyOf(var0));
   }

   public static CharSource wrap(CharSequence var0) {
      return (CharSource)(var0 instanceof String ? new CharSource.StringCharSource((String)var0) : new CharSource.CharSequenceCharSource(var0));
   }

   public static CharSource empty() {
      return CharSource.EmptyCharSource.INSTANCE;
   }

   private final class AsByteSource extends ByteSource {
      final Charset charset;

      AsByteSource(Charset var2) {
         this.charset = Preconditions.checkNotNull(var2);
      }

      @Override
      public CharSource asCharSource(Charset var1) {
         return var1.equals(this.charset) ? CharSource.this : super.asCharSource(var1);
      }

      @Override
      public InputStream openStream() throws IOException {
         return new ReaderInputStream(CharSource.this.openStream(), this.charset, 8192);
      }

      @Override
      public String toString() {
         String var1 = CharSource.this.toString();
         String var2 = String.valueOf(this.charset);
         return new StringBuilder(15 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".asByteSource(")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static class CharSequenceCharSource extends CharSource {
      private static final Splitter LINE_SPLITTER = Splitter.onPattern("\r\n|\n|\r");
      protected final CharSequence seq;

      protected CharSequenceCharSource(CharSequence var1) {
         this.seq = Preconditions.checkNotNull(var1);
      }

      @Override
      public Reader openStream() {
         return new CharSequenceReader(this.seq);
      }

      @Override
      public String read() {
         return this.seq.toString();
      }

      @Override
      public boolean isEmpty() {
         return this.seq.length() == 0;
      }

      @Override
      public long length() {
         return (long)this.seq.length();
      }

      @Override
      public Optional<Long> lengthIfKnown() {
         return Optional.of((long)this.seq.length());
      }

      private Iterator<String> linesIterator() {
         return new AbstractIterator<String>() {
            Iterator<String> lines = CharSource.CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();

            protected String computeNext() {
               if (this.lines.hasNext()) {
                  String var1 = this.lines.next();
                  if (this.lines.hasNext() || !var1.isEmpty()) {
                     return var1;
                  }
               }

               return this.endOfData();
            }
         };
      }

      @Override
      public Stream<String> lines() {
         return Streams.stream(this.linesIterator());
      }

      @Override
      public String readFirstLine() {
         Iterator var1 = this.linesIterator();
         return var1.hasNext() ? (String)var1.next() : null;
      }

      @Override
      public ImmutableList<String> readLines() {
         return ImmutableList.copyOf(this.linesIterator());
      }

      @ParametricNullness
      @Override
      public <T> T readLines(LineProcessor<T> var1) throws IOException {
         Iterator var2 = this.linesIterator();

         while (var2.hasNext() && var1.processLine((String)var2.next())) {
         }

         return (T)var1.getResult();
      }

      @Override
      public String toString() {
         String var1 = Ascii.truncate(this.seq, 30, "...");
         return new StringBuilder(17 + String.valueOf(var1).length()).append("CharSource.wrap(").append(var1).append(")").toString();
      }
   }

   private static final class ConcatenatedCharSource extends CharSource {
      private final Iterable<? extends CharSource> sources;

      ConcatenatedCharSource(Iterable<? extends CharSource> var1) {
         this.sources = Preconditions.checkNotNull(var1);
      }

      @Override
      public Reader openStream() throws IOException {
         return new MultiReader(this.sources.iterator());
      }

      @Override
      public boolean isEmpty() throws IOException {
         for (CharSource var2 : this.sources) {
            if (!var2.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      @Override
      public Optional<Long> lengthIfKnown() {
         long var1 = 0L;

         for (CharSource var4 : this.sources) {
            Optional var5 = var4.lengthIfKnown();
            if (!var5.isPresent()) {
               return Optional.absent();
            }

            var1 += var5.get();
         }

         return Optional.of(var1);
      }

      @Override
      public long length() throws IOException {
         long var1 = 0L;

         for (CharSource var4 : this.sources) {
            var1 += var4.length();
         }

         return var1;
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.sources);
         return new StringBuilder(19 + String.valueOf(var1).length()).append("CharSource.concat(").append(var1).append(")").toString();
      }
   }

   private static final class EmptyCharSource extends CharSource.StringCharSource {
      private static final CharSource.EmptyCharSource INSTANCE = new CharSource.EmptyCharSource();

      private EmptyCharSource() {
         super("");
      }

      @Override
      public String toString() {
         return "CharSource.empty()";
      }
   }

   private static class StringCharSource extends CharSource.CharSequenceCharSource {
      protected StringCharSource(String var1) {
         super(var1);
      }

      @Override
      public Reader openStream() {
         return new StringReader((String)this.seq);
      }

      @Override
      public long copyTo(Appendable var1) throws IOException {
         var1.append(this.seq);
         return (long)this.seq.length();
      }

      @Override
      public long copyTo(CharSink var1) throws IOException {
         Preconditions.checkNotNull(var1);
         Closer var2 = Closer.create();

         long var4;
         try {
            Writer var3 = var2.register(var1.openStream());
            var3.write((String)this.seq);
            var4 = (long)this.seq.length();
         } catch (Throwable var9) {
            throw var2.rethrow(var9);
         } finally {
            var2.close();
         }

         return var4;
      }
   }
}
