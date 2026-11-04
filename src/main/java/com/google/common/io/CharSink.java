package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class CharSink {
   protected CharSink() {
   }

   public abstract Writer openStream() throws IOException;

   public Writer openBufferedStream() throws IOException {
      Writer var1 = this.openStream();
      return var1 instanceof BufferedWriter ? (BufferedWriter)var1 : new BufferedWriter(var1);
   }

   public void write(CharSequence var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      try {
         Writer var3 = var2.register(this.openStream());
         var3.append(var1);
         var3.flush();
      } catch (Throwable var7) {
         throw var2.rethrow(var7);
      } finally {
         var2.close();
      }
   }

   public void writeLines(Iterable<? extends CharSequence> var1) throws IOException {
      this.writeLines(var1, System.getProperty("line.separator"));
   }

   public void writeLines(Iterable<? extends CharSequence> var1, String var2) throws IOException {
      this.writeLines(var1.iterator(), var2);
   }

   @Beta
   public void writeLines(Stream<? extends CharSequence> var1) throws IOException {
      this.writeLines(var1, System.getProperty("line.separator"));
   }

   @Beta
   public void writeLines(Stream<? extends CharSequence> var1, String var2) throws IOException {
      this.writeLines(var1.iterator(), var2);
   }

   private void writeLines(Iterator<? extends CharSequence> var1, String var2) throws IOException {
      Preconditions.checkNotNull(var2);
      Writer var3 = this.openBufferedStream();

      try {
         while (var1.hasNext()) {
            var3.append((CharSequence)var1.next()).append(var2);
         }
      } catch (Throwable var7) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (var3 != null) {
         var3.close();
      }
   }

   @CanIgnoreReturnValue
   public long writeFrom(Readable var1) throws IOException {
      Preconditions.checkNotNull(var1);
      Closer var2 = Closer.create();

      long var6;
      try {
         Writer var3 = var2.register(this.openStream());
         long var4 = CharStreams.copy(var1, var3);
         var3.flush();
         var6 = var4;
      } catch (Throwable var11) {
         throw var2.rethrow(var11);
      } finally {
         var2.close();
      }

      return var6;
   }
}
