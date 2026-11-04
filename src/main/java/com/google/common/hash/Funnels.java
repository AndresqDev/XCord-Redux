package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

@ElementTypesAreNonnullByDefault
@Beta
public final class Funnels {
   private Funnels() {
   }

   public static Funnel<byte[]> byteArrayFunnel() {
      return Funnels.ByteArrayFunnel.INSTANCE;
   }

   public static Funnel<CharSequence> unencodedCharsFunnel() {
      return Funnels.UnencodedCharsFunnel.INSTANCE;
   }

   public static Funnel<CharSequence> stringFunnel(Charset var0) {
      return new Funnels.StringCharsetFunnel(var0);
   }

   public static Funnel<Integer> integerFunnel() {
      return Funnels.IntegerFunnel.INSTANCE;
   }

   public static <E> Funnel<Iterable<? extends E>> sequentialFunnel(Funnel<E> var0) {
      return new Funnels.SequentialFunnel<>(var0);
   }

   public static Funnel<Long> longFunnel() {
      return Funnels.LongFunnel.INSTANCE;
   }

   public static OutputStream asOutputStream(PrimitiveSink var0) {
      return new Funnels.SinkAsStream(var0);
   }

   private static enum ByteArrayFunnel implements Funnel<byte[]> {
      INSTANCE;

      public void funnel(byte[] var1, PrimitiveSink var2) {
         var2.putBytes(var1);
      }

      @Override
      public String toString() {
         return "Funnels.byteArrayFunnel()";
      }
   }

   private static enum IntegerFunnel implements Funnel<Integer> {
      INSTANCE;

      public void funnel(Integer var1, PrimitiveSink var2) {
         var2.putInt(var1);
      }

      @Override
      public String toString() {
         return "Funnels.integerFunnel()";
      }
   }

   private static enum LongFunnel implements Funnel<Long> {
      INSTANCE;

      public void funnel(Long var1, PrimitiveSink var2) {
         var2.putLong(var1);
      }

      @Override
      public String toString() {
         return "Funnels.longFunnel()";
      }
   }

   private static class SequentialFunnel<E> implements Funnel<Iterable<? extends E>>, Serializable {
      private final Funnel<E> elementFunnel;

      SequentialFunnel(Funnel<E> var1) {
         this.elementFunnel = Preconditions.checkNotNull(var1);
      }

      public void funnel(Iterable<? extends E> var1, PrimitiveSink var2) {
         for (Object var4 : var1) {
            this.elementFunnel.funnel((E)var4, var2);
         }
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.elementFunnel);
         return new StringBuilder(26 + String.valueOf(var1).length()).append("Funnels.sequentialFunnel(").append(var1).append(")").toString();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Funnels.SequentialFunnel) {
            Funnels.SequentialFunnel var2 = (Funnels.SequentialFunnel)var1;
            return this.elementFunnel.equals(var2.elementFunnel);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Funnels.SequentialFunnel.class.hashCode() ^ this.elementFunnel.hashCode();
      }
   }

   private static class SinkAsStream extends OutputStream {
      final PrimitiveSink sink;

      SinkAsStream(PrimitiveSink var1) {
         this.sink = Preconditions.checkNotNull(var1);
      }

      @Override
      public void write(int var1) {
         this.sink.putByte((byte)var1);
      }

      @Override
      public void write(byte[] var1) {
         this.sink.putBytes(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         this.sink.putBytes(var1, var2, var3);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.sink);
         return new StringBuilder(24 + String.valueOf(var1).length()).append("Funnels.asOutputStream(").append(var1).append(")").toString();
      }
   }

   private static class StringCharsetFunnel implements Funnel<CharSequence>, Serializable {
      private final Charset charset;

      StringCharsetFunnel(Charset var1) {
         this.charset = Preconditions.checkNotNull(var1);
      }

      public void funnel(CharSequence var1, PrimitiveSink var2) {
         var2.putString(var1, this.charset);
      }

      @Override
      public String toString() {
         String var1 = this.charset.name();
         return new StringBuilder(22 + String.valueOf(var1).length()).append("Funnels.stringFunnel(").append(var1).append(")").toString();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Funnels.StringCharsetFunnel) {
            Funnels.StringCharsetFunnel var2 = (Funnels.StringCharsetFunnel)var1;
            return this.charset.equals(var2.charset);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Funnels.StringCharsetFunnel.class.hashCode() ^ this.charset.hashCode();
      }

      Object writeReplace() {
         return new Funnels.StringCharsetFunnel.SerializedForm(this.charset);
      }

      private static class SerializedForm implements Serializable {
         private final String charsetCanonicalName;
         private static final long serialVersionUID = 0L;

         SerializedForm(Charset var1) {
            this.charsetCanonicalName = var1.name();
         }

         private Object readResolve() {
            return Funnels.stringFunnel(Charset.forName(this.charsetCanonicalName));
         }
      }
   }

   private static enum UnencodedCharsFunnel implements Funnel<CharSequence> {
      INSTANCE;

      public void funnel(CharSequence var1, PrimitiveSink var2) {
         var2.putUnencodedChars(var1);
      }

      @Override
      public String toString() {
         return "Funnels.unencodedCharsFunnel()";
      }
   }
}
