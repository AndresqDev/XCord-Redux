package com.google.protobuf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public abstract class ByteString implements Serializable, Iterable<Byte> {
   static final int CONCATENATE_BY_COPY_SIZE = 128;
   static final int MIN_READ_FROM_CHUNK_SIZE = 256;
   static final int MAX_READ_FROM_CHUNK_SIZE = 8192;
   public static final ByteString EMPTY = new ByteString.LiteralByteString(Internal.EMPTY_BYTE_ARRAY);
   private static final ByteString.ByteArrayCopier byteArrayCopier = (ByteString.ByteArrayCopier)(Android.isOnAndroidDevice()
      ? new ByteString.SystemByteArrayCopier()
      : new ByteString.ArraysByteArrayCopier());
   private int hash = 0;
   private static final int UNSIGNED_BYTE_MASK = 255;
   private static final Comparator<ByteString> UNSIGNED_LEXICOGRAPHICAL_COMPARATOR = new Comparator<ByteString>() {
      public int compare(ByteString var1, ByteString var2) {
         ByteString.ByteIterator var3 = var1.iterator();
         ByteString.ByteIterator var4 = var2.iterator();

         while (var3.hasNext() && var4.hasNext()) {
            int var5 = Integer.compare(ByteString.toInt(var3.nextByte()), ByteString.toInt(var4.nextByte()));
            if (var5 != 0) {
               return var5;
            }
         }

         return Integer.compare(var1.size(), var2.size());
      }
   };

   ByteString() {
   }

   public abstract byte byteAt(int var1);

   abstract byte internalByteAt(int var1);

   public ByteString.ByteIterator iterator() {
      return new ByteString.AbstractByteIterator() {
         private int position = 0;
         private final int limit = ByteString.this.size();

         @Override
         public boolean hasNext() {
            return this.position < this.limit;
         }

         @Override
         public byte nextByte() {
            int var1 = this.position;
            if (var1 >= this.limit) {
               throw new NoSuchElementException();
            } else {
               this.position = var1 + 1;
               return ByteString.this.internalByteAt(var1);
            }
         }
      };
   }

   public abstract int size();

   public final boolean isEmpty() {
      return this.size() == 0;
   }

   private static int toInt(byte var0) {
      return var0 & 0xFF;
   }

   public static Comparator<ByteString> unsignedLexicographicalComparator() {
      return UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
   }

   public final ByteString substring(int var1) {
      return this.substring(var1, this.size());
   }

   public abstract ByteString substring(int var1, int var2);

   public final boolean startsWith(ByteString var1) {
      return this.size() >= var1.size() && this.substring(0, var1.size()).equals(var1);
   }

   public final boolean endsWith(ByteString var1) {
      return this.size() >= var1.size() && this.substring(this.size() - var1.size()).equals(var1);
   }

   public static ByteString copyFrom(byte[] var0, int var1, int var2) {
      checkRange(var1, var1 + var2, var0.length);
      return new ByteString.LiteralByteString(byteArrayCopier.copyFrom(var0, var1, var2));
   }

   public static ByteString copyFrom(byte[] var0) {
      return copyFrom(var0, 0, var0.length);
   }

   static ByteString wrap(ByteBuffer var0) {
      if (var0.hasArray()) {
         int var1 = var0.arrayOffset();
         return wrap(var0.array(), var1 + var0.position(), var0.remaining());
      } else {
         return new NioByteString(var0);
      }
   }

   static ByteString wrap(byte[] var0) {
      return new ByteString.LiteralByteString(var0);
   }

   static ByteString wrap(byte[] var0, int var1, int var2) {
      return new ByteString.BoundedByteString(var0, var1, var2);
   }

   public static ByteString copyFrom(ByteBuffer var0, int var1) {
      checkRange(0, var1, var0.remaining());
      byte[] var2 = new byte[var1];
      var0.get(var2);
      return new ByteString.LiteralByteString(var2);
   }

   public static ByteString copyFrom(ByteBuffer var0) {
      return copyFrom(var0, var0.remaining());
   }

   public static ByteString copyFrom(String var0, String var1) throws UnsupportedEncodingException {
      return new ByteString.LiteralByteString(var0.getBytes(var1));
   }

   public static ByteString copyFrom(String var0, Charset var1) {
      return new ByteString.LiteralByteString(var0.getBytes(var1));
   }

   public static ByteString copyFromUtf8(String var0) {
      return new ByteString.LiteralByteString(var0.getBytes(Internal.UTF_8));
   }

   public static ByteString readFrom(InputStream var0) throws IOException {
      return readFrom(var0, 256, 8192);
   }

   public static ByteString readFrom(InputStream var0, int var1) throws IOException {
      return readFrom(var0, var1, var1);
   }

   public static ByteString readFrom(InputStream var0, int var1, int var2) throws IOException {
      ArrayList var3 = new ArrayList();
      int var4 = var1;

      while (true) {
         ByteString var5 = readChunk(var0, var4);
         if (var5 == null) {
            return copyFrom(var3);
         }

         var3.add(var5);
         var4 = Math.min(var4 * 2, var2);
      }
   }

   private static ByteString readChunk(InputStream var0, int var1) throws IOException {
      byte[] var2 = new byte[var1];
      int var3 = 0;

      while (var3 < var1) {
         int var4 = var0.read(var2, var3, var1 - var3);
         if (var4 == -1) {
            break;
         }

         var3 += var4;
      }

      return var3 == 0 ? null : copyFrom(var2, 0, var3);
   }

   public final ByteString concat(ByteString var1) {
      if (Integer.MAX_VALUE - this.size() < var1.size()) {
         throw new IllegalArgumentException("ByteString would be too long: " + this.size() + "+" + var1.size());
      } else {
         return RopeByteString.concatenate(this, var1);
      }
   }

   public static ByteString copyFrom(Iterable<ByteString> var0) {
      int var1;
      if (!(var0 instanceof Collection)) {
         int var2 = 0;

         for (Iterator var3 = var0.iterator(); var3.hasNext(); var2++) {
            var3.next();
         }

         var1 = var2;
      } else {
         var1 = ((Collection)var0).size();
      }

      return var1 == 0 ? EMPTY : balancedConcat(var0.iterator(), var1);
   }

   private static ByteString balancedConcat(Iterator<ByteString> var0, int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException(String.format("length (%s) must be >= 1", var1));
      } else {
         ByteString var2;
         if (var1 == 1) {
            var2 = (ByteString)var0.next();
         } else {
            int var3 = var1 >>> 1;
            ByteString var4 = balancedConcat(var0, var3);
            ByteString var5 = balancedConcat(var0, var1 - var3);
            var2 = var4.concat(var5);
         }

         return var2;
      }
   }

   public void copyTo(byte[] var1, int var2) {
      this.copyTo(var1, 0, var2, this.size());
   }

   @Deprecated
   public final void copyTo(byte[] var1, int var2, int var3, int var4) {
      checkRange(var2, var2 + var4, this.size());
      checkRange(var3, var3 + var4, var1.length);
      if (var4 > 0) {
         this.copyToInternal(var1, var2, var3, var4);
      }
   }

   protected abstract void copyToInternal(byte[] var1, int var2, int var3, int var4);

   public abstract void copyTo(ByteBuffer var1);

   public final byte[] toByteArray() {
      int var1 = this.size();
      if (var1 == 0) {
         return Internal.EMPTY_BYTE_ARRAY;
      } else {
         byte[] var2 = new byte[var1];
         this.copyToInternal(var2, 0, 0, var1);
         return var2;
      }
   }

   public abstract void writeTo(OutputStream var1) throws IOException;

   final void writeTo(OutputStream var1, int var2, int var3) throws IOException {
      checkRange(var2, var2 + var3, this.size());
      if (var3 > 0) {
         this.writeToInternal(var1, var2, var3);
      }
   }

   abstract void writeToInternal(OutputStream var1, int var2, int var3) throws IOException;

   abstract void writeTo(ByteOutput var1) throws IOException;

   abstract void writeToReverse(ByteOutput var1) throws IOException;

   public abstract ByteBuffer asReadOnlyByteBuffer();

   public abstract List<ByteBuffer> asReadOnlyByteBufferList();

   public final String toString(String var1) throws UnsupportedEncodingException {
      try {
         return this.toString(Charset.forName(var1));
      } catch (UnsupportedCharsetException var4) {
         UnsupportedEncodingException var3 = new UnsupportedEncodingException(var1);
         var3.initCause(var4);
         throw var3;
      }
   }

   public final String toString(Charset var1) {
      return this.size() == 0 ? "" : this.toStringInternal(var1);
   }

   protected abstract String toStringInternal(Charset var1);

   public final String toStringUtf8() {
      return this.toString(Internal.UTF_8);
   }

   public abstract boolean isValidUtf8();

   protected abstract int partialIsValidUtf8(int var1, int var2, int var3);

   @Override
   public abstract boolean equals(Object var1);

   @Override
   public final int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         int var2 = this.size();
         var1 = this.partialHash(var2, 0, var2);
         if (var1 == 0) {
            var1 = 1;
         }

         this.hash = var1;
      }

      return var1;
   }

   public abstract InputStream newInput();

   public abstract CodedInputStream newCodedInput();

   public static ByteString.Output newOutput(int var0) {
      return new ByteString.Output(var0);
   }

   public static ByteString.Output newOutput() {
      return new ByteString.Output(128);
   }

   static ByteString.CodedBuilder newCodedBuilder(int var0) {
      return new ByteString.CodedBuilder(var0);
   }

   protected abstract int getTreeDepth();

   protected abstract boolean isBalanced();

   protected final int peekCachedHashCode() {
      return this.hash;
   }

   protected abstract int partialHash(int var1, int var2, int var3);

   static void checkIndex(int var0, int var1) {
      if ((var0 | var1 - (var0 + 1)) < 0) {
         if (var0 < 0) {
            throw new ArrayIndexOutOfBoundsException("Index < 0: " + var0);
         } else {
            throw new ArrayIndexOutOfBoundsException("Index > length: " + var0 + ", " + var1);
         }
      }
   }

   static int checkRange(int var0, int var1, int var2) {
      int var3 = var1 - var0;
      if ((var0 | var1 | var3 | var2 - var1) < 0) {
         if (var0 < 0) {
            throw new IndexOutOfBoundsException("Beginning index: " + var0 + " < 0");
         } else if (var1 < var0) {
            throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + var0 + ", " + var1);
         } else {
            throw new IndexOutOfBoundsException("End index: " + var1 + " >= " + var2);
         }
      } else {
         return var3;
      }
   }

   @Override
   public final String toString() {
      return String.format(
         Locale.ROOT,
         "<ByteString@%s size=%d contents=\"%s\">",
         Integer.toHexString(System.identityHashCode(this)),
         this.size(),
         this.truncateAndEscapeForDisplay()
      );
   }

   private String truncateAndEscapeForDisplay() {
      byte var1 = 50;
      return this.size() <= 50 ? TextFormatEscaper.escapeBytes(this) : TextFormatEscaper.escapeBytes(this.substring(0, 47)) + "...";
   }

   abstract static class AbstractByteIterator implements ByteString.ByteIterator {
      public final Byte next() {
         return this.nextByte();
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static final class ArraysByteArrayCopier implements ByteString.ByteArrayCopier {
      private ArraysByteArrayCopier() {
      }

      @Override
      public byte[] copyFrom(byte[] var1, int var2, int var3) {
         return Arrays.copyOfRange(var1, var2, var2 + var3);
      }
   }

   private static final class BoundedByteString extends ByteString.LiteralByteString {
      private final int bytesOffset;
      private final int bytesLength;
      private static final long serialVersionUID = 1L;

      BoundedByteString(byte[] var1, int var2, int var3) {
         super(var1);
         checkRange(var2, var2 + var3, var1.length);
         this.bytesOffset = var2;
         this.bytesLength = var3;
      }

      @Override
      public byte byteAt(int var1) {
         checkIndex(var1, this.size());
         return this.bytes[this.bytesOffset + var1];
      }

      @Override
      byte internalByteAt(int var1) {
         return this.bytes[this.bytesOffset + var1];
      }

      @Override
      public int size() {
         return this.bytesLength;
      }

      @Override
      protected int getOffsetIntoBytes() {
         return this.bytesOffset;
      }

      @Override
      protected void copyToInternal(byte[] var1, int var2, int var3, int var4) {
         System.arraycopy(this.bytes, this.getOffsetIntoBytes() + var2, var1, var3, var4);
      }

      Object writeReplace() {
         return ByteString.wrap(this.toByteArray());
      }

      private void readObject(ObjectInputStream var1) throws IOException {
         throw new InvalidObjectException("BoundedByteStream instances are not to be serialized directly");
      }
   }

   private interface ByteArrayCopier {
      byte[] copyFrom(byte[] var1, int var2, int var3);
   }

   public interface ByteIterator extends Iterator<Byte> {
      byte nextByte();
   }

   static final class CodedBuilder {
      private final CodedOutputStream output;
      private final byte[] buffer;

      private CodedBuilder(int var1) {
         this.buffer = new byte[var1];
         this.output = CodedOutputStream.newInstance(this.buffer);
      }

      public ByteString build() {
         this.output.checkNoSpaceLeft();
         return new ByteString.LiteralByteString(this.buffer);
      }

      public CodedOutputStream getCodedOutput() {
         return this.output;
      }
   }

   abstract static class LeafByteString extends ByteString {
      @Override
      protected final int getTreeDepth() {
         return 0;
      }

      @Override
      protected final boolean isBalanced() {
         return true;
      }

      @Override
      void writeToReverse(ByteOutput var1) throws IOException {
         this.writeTo(var1);
      }

      abstract boolean equalsRange(ByteString var1, int var2, int var3);
   }

   private static class LiteralByteString extends ByteString.LeafByteString {
      private static final long serialVersionUID = 1L;
      protected final byte[] bytes;

      LiteralByteString(byte[] var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.bytes = var1;
         }
      }

      @Override
      public byte byteAt(int var1) {
         return this.bytes[var1];
      }

      @Override
      byte internalByteAt(int var1) {
         return this.bytes[var1];
      }

      @Override
      public int size() {
         return this.bytes.length;
      }

      @Override
      public final ByteString substring(int var1, int var2) {
         int var3 = checkRange(var1, var2, this.size());
         return (ByteString)(var3 == 0 ? ByteString.EMPTY : new ByteString.BoundedByteString(this.bytes, this.getOffsetIntoBytes() + var1, var3));
      }

      @Override
      protected void copyToInternal(byte[] var1, int var2, int var3, int var4) {
         System.arraycopy(this.bytes, var2, var1, var3, var4);
      }

      @Override
      public final void copyTo(ByteBuffer var1) {
         var1.put(this.bytes, this.getOffsetIntoBytes(), this.size());
      }

      @Override
      public final ByteBuffer asReadOnlyByteBuffer() {
         return ByteBuffer.wrap(this.bytes, this.getOffsetIntoBytes(), this.size()).asReadOnlyBuffer();
      }

      @Override
      public final List<ByteBuffer> asReadOnlyByteBufferList() {
         return Collections.singletonList(this.asReadOnlyByteBuffer());
      }

      @Override
      public final void writeTo(OutputStream var1) throws IOException {
         var1.write(this.toByteArray());
      }

      @Override
      final void writeToInternal(OutputStream var1, int var2, int var3) throws IOException {
         var1.write(this.bytes, this.getOffsetIntoBytes() + var2, var3);
      }

      @Override
      final void writeTo(ByteOutput var1) throws IOException {
         var1.writeLazy(this.bytes, this.getOffsetIntoBytes(), this.size());
      }

      @Override
      protected final String toStringInternal(Charset var1) {
         return new String(this.bytes, this.getOffsetIntoBytes(), this.size(), var1);
      }

      @Override
      public final boolean isValidUtf8() {
         int var1 = this.getOffsetIntoBytes();
         return Utf8.isValidUtf8(this.bytes, var1, var1 + this.size());
      }

      @Override
      protected final int partialIsValidUtf8(int var1, int var2, int var3) {
         int var4 = this.getOffsetIntoBytes() + var2;
         return Utf8.partialIsValidUtf8(var1, this.bytes, var4, var4 + var3);
      }

      @Override
      public final boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ByteString)) {
            return false;
         } else if (this.size() != ((ByteString)var1).size()) {
            return false;
         } else if (this.size() == 0) {
            return true;
         } else if (var1 instanceof ByteString.LiteralByteString) {
            ByteString.LiteralByteString var2 = (ByteString.LiteralByteString)var1;
            int var3 = this.peekCachedHashCode();
            int var4 = var2.peekCachedHashCode();
            return var3 != 0 && var4 != 0 && var3 != var4 ? false : this.equalsRange((ByteString.LiteralByteString)var1, 0, this.size());
         } else {
            return var1.equals(this);
         }
      }

      @Override
      final boolean equalsRange(ByteString var1, int var2, int var3) {
         if (var3 > var1.size()) {
            throw new IllegalArgumentException("Length too large: " + var3 + this.size());
         } else if (var2 + var3 > var1.size()) {
            throw new IllegalArgumentException("Ran off end of other: " + var2 + ", " + var3 + ", " + var1.size());
         } else if (var1 instanceof ByteString.LiteralByteString) {
            ByteString.LiteralByteString var4 = (ByteString.LiteralByteString)var1;
            byte[] var5 = this.bytes;
            byte[] var6 = var4.bytes;
            int var7 = this.getOffsetIntoBytes() + var3;
            int var8 = this.getOffsetIntoBytes();

            for (int var9 = var4.getOffsetIntoBytes() + var2; var8 < var7; var9++) {
               if (var5[var8] != var6[var9]) {
                  return false;
               }

               var8++;
            }

            return true;
         } else {
            return var1.substring(var2, var2 + var3).equals(this.substring(0, var3));
         }
      }

      @Override
      protected final int partialHash(int var1, int var2, int var3) {
         return Internal.partialHash(var1, this.bytes, this.getOffsetIntoBytes() + var2, var3);
      }

      @Override
      public final InputStream newInput() {
         return new ByteArrayInputStream(this.bytes, this.getOffsetIntoBytes(), this.size());
      }

      @Override
      public final CodedInputStream newCodedInput() {
         return CodedInputStream.newInstance(this.bytes, this.getOffsetIntoBytes(), this.size(), true);
      }

      protected int getOffsetIntoBytes() {
         return 0;
      }
   }

   public static final class Output extends OutputStream {
      private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
      private final int initialCapacity;
      private final ArrayList<ByteString> flushedBuffers;
      private int flushedBuffersTotalBytes;
      private byte[] buffer;
      private int bufferPos;

      Output(int var1) {
         if (var1 < 0) {
            throw new IllegalArgumentException("Buffer size < 0");
         } else {
            this.initialCapacity = var1;
            this.flushedBuffers = new ArrayList<>();
            this.buffer = new byte[var1];
         }
      }

      @Override
      public synchronized void write(int var1) {
         if (this.bufferPos == this.buffer.length) {
            this.flushFullBuffer(1);
         }

         this.buffer[this.bufferPos++] = (byte)var1;
      }

      @Override
      public synchronized void write(byte[] var1, int var2, int var3) {
         if (var3 <= this.buffer.length - this.bufferPos) {
            System.arraycopy(var1, var2, this.buffer, this.bufferPos, var3);
            this.bufferPos += var3;
         } else {
            int var4 = this.buffer.length - this.bufferPos;
            System.arraycopy(var1, var2, this.buffer, this.bufferPos, var4);
            var2 += var4;
            var3 -= var4;
            this.flushFullBuffer(var3);
            System.arraycopy(var1, var2, this.buffer, 0, var3);
            this.bufferPos = var3;
         }
      }

      public synchronized ByteString toByteString() {
         this.flushLastBuffer();
         return ByteString.copyFrom(this.flushedBuffers);
      }

      private byte[] copyArray(byte[] var1, int var2) {
         byte[] var3 = new byte[var2];
         System.arraycopy(var1, 0, var3, 0, Math.min(var1.length, var2));
         return var3;
      }

      public void writeTo(OutputStream var1) throws IOException {
         ByteString[] var2;
         byte[] var3;
         int var4;
         synchronized (this) {
            var2 = this.flushedBuffers.toArray(new ByteString[this.flushedBuffers.size()]);
            var3 = this.buffer;
            var4 = this.bufferPos;
         }

         for (ByteString var8 : var2) {
            var8.writeTo(var1);
         }

         var1.write(this.copyArray(var3, var4));
      }

      public synchronized int size() {
         return this.flushedBuffersTotalBytes + this.bufferPos;
      }

      public synchronized void reset() {
         this.flushedBuffers.clear();
         this.flushedBuffersTotalBytes = 0;
         this.bufferPos = 0;
      }

      @Override
      public String toString() {
         return String.format("<ByteString.Output@%s size=%d>", Integer.toHexString(System.identityHashCode(this)), this.size());
      }

      private void flushFullBuffer(int var1) {
         this.flushedBuffers.add(new ByteString.LiteralByteString(this.buffer));
         this.flushedBuffersTotalBytes = this.flushedBuffersTotalBytes + this.buffer.length;
         int var2 = Math.max(this.initialCapacity, Math.max(var1, this.flushedBuffersTotalBytes >>> 1));
         this.buffer = new byte[var2];
         this.bufferPos = 0;
      }

      private void flushLastBuffer() {
         if (this.bufferPos < this.buffer.length) {
            if (this.bufferPos > 0) {
               byte[] var1 = this.copyArray(this.buffer, this.bufferPos);
               this.flushedBuffers.add(new ByteString.LiteralByteString(var1));
            }
         } else {
            this.flushedBuffers.add(new ByteString.LiteralByteString(this.buffer));
            this.buffer = EMPTY_BYTE_ARRAY;
         }

         this.flushedBuffersTotalBytes = this.flushedBuffersTotalBytes + this.bufferPos;
         this.bufferPos = 0;
      }
   }

   private static final class SystemByteArrayCopier implements ByteString.ByteArrayCopier {
      private SystemByteArrayCopier() {
      }

      @Override
      public byte[] copyFrom(byte[] var1, int var2, int var3) {
         byte[] var4 = new byte[var3];
         System.arraycopy(var1, var2, var4, 0, var3);
         return var4;
      }
   }
}
