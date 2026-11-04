package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ByteSource {
   protected ByteSource() {
   }

   public CharSource asCharSource(Charset var1) {
      return new ByteSource.AsCharSource(var1);
   }

   public abstract InputStream openStream() throws IOException;

   public InputStream openBufferedStream() throws IOException {
      InputStream var1 = this.openStream();
      return var1 instanceof BufferedInputStream ? (BufferedInputStream)var1 : new BufferedInputStream(var1);
   }

   public ByteSource slice(long var1, long var3) {
      return new ByteSource.SlicedByteSource(var1, var3);
   }

   public boolean isEmpty() throws IOException {
      Optional var1 = this.sizeIfKnown();
      if (var1.isPresent()) {
         return (Long)var1.get() == 0L;
      } else {
         Closer var2 = Closer.create();

         boolean var4;
         try {
            InputStream var3 = var2.register(this.openStream());
            var4 = var3.read() == -1;
         } catch (Throwable var8) {
            throw var2.rethrow(var8);
         } finally {
            var2.close();
         }

         return var4;
      }
   }

   @Beta
   public Optional<Long> sizeIfKnown() {
      return Optional.absent();
   }

   public long size() throws IOException {
      Optional var1 = this.sizeIfKnown();
      if (var1.isPresent()) {
         return (Long)var1.get();
      } else {
         Closer var2 = Closer.create();

         try {
            InputStream var21 = var2.register(this.openStream());
            return this.countBySkipping(var21);
         } catch (IOException var18) {
         } finally {
            var2.close();
         }

         var2 = Closer.create();

         long var4;
         try {
            InputStream var3 = var2.register(this.openStream());
            var4 = ByteStreams.exhaust(var3);
         } catch (Throwable var16) {
            throw var2.rethrow(var16);
         } finally {
            var2.close();
         }

         return var4;
      }
   }

   private long countBySkipping(InputStream var1) throws IOException {
      long var2 = 0L;

      long var4;
      while ((var4 = ByteStreams.skipUpTo(var1, 2147483647L)) > 0L) {
         var2 += var4;
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public long copyTo(OutputStream var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var4;
      try {
         InputStream var3 = var2.register(this.openStream());
         var4 = ByteStreams.copy(var3, var1);
      } catch (Throwable var9) {
         throw var2.rethrow(var9);
      } finally {
         var2.close();
      }

      return var4;
   }

   @CanIgnoreReturnValue
   public long copyTo(ByteSink var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var5;
      try {
         InputStream var3 = var2.register(this.openStream());
         OutputStream var4 = var2.register(var1.openStream());
         var5 = ByteStreams.copy(var3, var4);
      } catch (Throwable var10) {
         throw var2.rethrow(var10);
      } finally {
         var2.close();
      }

      return var5;
   }

   public byte[] read() throws IOException {
      Closer var1 = Closer.create();

      byte[] var4;
      try {
         InputStream var2 = var1.register(this.openStream());
         Optional var3 = this.sizeIfKnown();
         var4 = var3.isPresent() ? ByteStreams.toByteArray(var2, (Long)var3.get()) : ByteStreams.toByteArray(var2);
      } catch (Throwable var8) {
         throw var1.rethrow(var8);
      } finally {
         var1.close();
      }

      return var4;
   }

   @Beta
   @CanIgnoreReturnValue
   public <T> T read(ByteProcessor<T> var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      Object var4;
      try {
         InputStream var3 = var2.register(this.openStream());
         var4 = ByteStreams.readBytes(var3, var1);
      } catch (Throwable var8) {
         throw var2.rethrow(var8);
      } finally {
         var2.close();
      }

      return (T)var4;
   }

   public HashCode hash(HashFunction var1) throws IOException {
      Hasher var2 = var1.newHasher();
      this.copyTo(Funnels.asOutputStream(var2));
      return var2.hash();
   }

   public boolean contentEquals(ByteSource var1) throws IOException {
      Preconditions.checkNotNull(var1);
      byte[] var2 = ByteStreams.createBuffer();
      byte[] var3 = ByteStreams.createBuffer();
      Closer var4 = Closer.create();

      try {
         InputStream var5 = var4.register(this.openStream());
         InputStream var6 = var4.register(var1.openStream());

         int var7;
         do {
            var7 = ByteStreams.read(var5, var2, 0, var2.length);
            int var8 = ByteStreams.read(var6, var3, 0, var3.length);
            if (var7 != var8 || !Arrays.equals(var2, var3)) {
               return false;
            }
         } while (var7 == var2.length);

         return true;
      } catch (Throwable var13) {
         throw var4.rethrow(var13);
      } finally {
         var4.close();
      }
   }

   public static ByteSource concat(Iterable<? extends ByteSource> var0) {
      return new ByteSource.ConcatenatedByteSource(var0);
   }

   public static ByteSource concat(Iterator<? extends ByteSource> var0) {
      return concat(ImmutableList.copyOf(var0));
   }

   public static ByteSource concat(ByteSource... var0) {
      return concat(ImmutableList.copyOf(var0));
   }

   public static ByteSource wrap(byte[] var0) {
      return new ByteSource.ByteArrayByteSource(var0);
   }

   public static ByteSource empty() {
      return ByteSource.EmptyByteSource.INSTANCE;
   }

   class AsCharSource extends CharSource {
      final Charset charset;

      AsCharSource(Charset var2) {
         this.charset = Preconditions.checkNotNull(var2);
      }

      @Override
      public ByteSource asByteSource(Charset var1) {
         return var1.equals(this.charset) ? ByteSource.this : super.asByteSource(var1);
      }

      @Override
      public Reader openStream() throws IOException {
         return new InputStreamReader(ByteSource.this.openStream(), this.charset);
      }

      @Override
      public String read() throws IOException {
         return new String(ByteSource.this.read(), this.charset);
      }

      @Override
      public String toString() {
         String var1 = ByteSource.this.toString();
         String var2 = String.valueOf(this.charset);
         return new StringBuilder(15 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append(var1)
            .append(".asCharSource(")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static class ByteArrayByteSource extends ByteSource {
      final byte[] bytes;
      final int offset;
      final int length;

      ByteArrayByteSource(byte[] var1) {
         this(var1, 0, var1.length);
      }

      ByteArrayByteSource(byte[] var1, int var2, int var3) {
         this.bytes = var1;
         this.offset = var2;
         this.length = var3;
      }

      @Override
      public InputStream openStream() {
         return new ByteArrayInputStream(this.bytes, this.offset, this.length);
      }

      @Override
      public InputStream openBufferedStream() throws IOException {
         return this.openStream();
      }

      @Override
      public boolean isEmpty() {
         return this.length == 0;
      }

      @Override
      public long size() {
         return (long)this.length;
      }

      @Override
      public Optional<Long> sizeIfKnown() {
         return Optional.of((long)this.length);
      }

      @Override
      public byte[] read() {
         return Arrays.copyOfRange(this.bytes, this.offset, this.offset + this.length);
      }

      @ParametricNullness
      @Override
      public <T> T read(ByteProcessor<T> var1) throws IOException {
         var1.processBytes(this.bytes, this.offset, this.length);
         return (T)var1.getResult();
      }

      @Override
      public long copyTo(OutputStream var1) throws IOException {
         var1.write(this.bytes, this.offset, this.length);
         return (long)this.length;
      }

      @Override
      public HashCode hash(HashFunction var1) throws IOException {
         return var1.hashBytes(this.bytes, this.offset, this.length);
      }

      @Override
      public ByteSource slice(long var1, long var3) {
         Preconditions.checkArgument(var1 >= 0L, "offset (%s) may not be negative", var1);
         Preconditions.checkArgument(var3 >= 0L, "length (%s) may not be negative", var3);
         var1 = Math.min(var1, (long)this.length);
         var3 = Math.min(var3, (long)this.length - var1);
         int var5 = this.offset + (int)var1;
         return new ByteSource.ByteArrayByteSource(this.bytes, var5, (int)var3);
      }

      @Override
      public String toString() {
         String var1 = Ascii.truncate(BaseEncoding.base16().encode(this.bytes, this.offset, this.length), 30, "...");
         return new StringBuilder(17 + String.valueOf(var1).length()).append("ByteSource.wrap(").append(var1).append(")").toString();
      }
   }

   private static final class ConcatenatedByteSource extends ByteSource {
      final Iterable<? extends ByteSource> sources;

      ConcatenatedByteSource(Iterable<? extends ByteSource> var1) {
         this.sources = Preconditions.checkNotNull(var1);
      }

      @Override
      public InputStream openStream() throws IOException {
         return new MultiInputStream(this.sources.iterator());
      }

      @Override
      public boolean isEmpty() throws IOException {
         for (ByteSource var2 : this.sources) {
            if (!var2.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      @Override
      public Optional<Long> sizeIfKnown() {
         if (!(this.sources instanceof Collection)) {
            return Optional.absent();
         } else {
            long var1 = 0L;

            for (ByteSource var4 : this.sources) {
               Optional var5 = var4.sizeIfKnown();
               if (!var5.isPresent()) {
                  return Optional.absent();
               }

               var1 += var5.get();
               if (var1 < 0L) {
                  return Optional.of(Long.MAX_VALUE);
               }
            }

            return Optional.of(var1);
         }
      }

      @Override
      public long size() throws IOException {
         long var1 = 0L;

         for (ByteSource var4 : this.sources) {
            var1 += var4.size();
            if (var1 < 0L) {
               return Long.MAX_VALUE;
            }
         }

         return var1;
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.sources);
         return new StringBuilder(19 + String.valueOf(var1).length()).append("ByteSource.concat(").append(var1).append(")").toString();
      }
   }

   private static final class EmptyByteSource extends ByteSource.ByteArrayByteSource {
      static final ByteSource.EmptyByteSource INSTANCE = new ByteSource.EmptyByteSource();

      EmptyByteSource() {
         super(new byte[0]);
      }

      @Override
      public CharSource asCharSource(Charset var1) {
         Preconditions.checkNotNull(var1);
         return CharSource.empty();
      }

      @Override
      public byte[] read() {
         return this.bytes;
      }

      @Override
      public String toString() {
         return "ByteSource.empty()";
      }
   }

   private final class SlicedByteSource extends ByteSource {
      final long offset;
      final long length;

      SlicedByteSource(long var2, long var4) {
         Preconditions.checkArgument(var2 >= 0L, "offset (%s) may not be negative", var2);
         Preconditions.checkArgument(var4 >= 0L, "length (%s) may not be negative", var4);
         this.offset = var2;
         this.length = var4;
      }

      @Override
      public InputStream openStream() throws IOException {
         return this.sliceStream(ByteSource.this.openStream());
      }

      @Override
      public InputStream openBufferedStream() throws IOException {
         return this.sliceStream(ByteSource.this.openBufferedStream());
      }

      private InputStream sliceStream(InputStream var1) throws IOException {
         if (this.offset > 0L) {
            long var2;
            try {
               var2 = ByteStreams.skipUpTo(var1, this.offset);
            } catch (Throwable var10) {
               Throwable var4 = var10;
               Closer var5 = Closer.create();
               var5.register(var1);

               try {
                  throw var5.rethrow(var4);
               } finally {
                  var5.close();
               }
            }

            if (var2 < this.offset) {
               var1.close();
               return new ByteArrayInputStream(new byte[0]);
            }
         }

         return ByteStreams.limit(var1, this.length);
      }

      @Override
      public ByteSource slice(long var1, long var3) {
         Preconditions.checkArgument(var1 >= 0L, "offset (%s) may not be negative", var1);
         Preconditions.checkArgument(var3 >= 0L, "length (%s) may not be negative", var3);
         long var5 = this.length - var1;
         return var5 <= 0L ? ByteSource.empty() : ByteSource.this.slice(this.offset + var1, Math.min(var3, var5));
      }

      @Override
      public boolean isEmpty() throws IOException {
         return this.length == 0L || super.isEmpty();
      }

      @Override
      public Optional<Long> sizeIfKnown() {
         Optional var1 = ByteSource.this.sizeIfKnown();
         if (var1.isPresent()) {
            long var2 = (Long)var1.get();
            long var4 = Math.min(this.offset, var2);
            return Optional.of(Math.min(this.length, var2 - var4));
         } else {
            return Optional.absent();
         }
      }

      @Override
      public String toString() {
         String var1 = ByteSource.this.toString();
         long var2 = this.offset;
         long var4 = this.length;
         return new StringBuilder(50 + String.valueOf(var1).length())
            .append(var1)
            .append(".slice(")
            .append(var2)
            .append(", ")
            .append(var4)
            .append(")")
            .toString();
      }
   }
}
