package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class Closer implements Closeable {
   private static final Closer.Suppressor SUPPRESSOR;
   @VisibleForTesting
   final Closer.Suppressor suppressor;
   private final Deque<Closeable> stack = new ArrayDeque<>(4);
   private Throwable thrown;

   public static Closer create() {
      return new Closer(SUPPRESSOR);
   }

   @VisibleForTesting
   Closer(Closer.Suppressor var1) {
      this.suppressor = Preconditions.checkNotNull(var1);
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   public <C extends Closeable> C register(@ParametricNullness C var1) {
      if (var1 != null) {
         this.stack.addFirst(var1);
      }

      return (C)var1;
   }

   public RuntimeException rethrow(Throwable var1) throws IOException {
      Preconditions.checkNotNull(var1);
      this.thrown = var1;
      Throwables.propagateIfPossible(var1, IOException.class);
      throw new RuntimeException(var1);
   }

   public <X extends Exception> RuntimeException rethrow(Throwable var1, Class<X> var2) throws IOException, X {
      Preconditions.checkNotNull(var1);
      this.thrown = var1;
      Throwables.propagateIfPossible(var1, IOException.class);
      Throwables.propagateIfPossible(var1, var2);
      throw new RuntimeException(var1);
   }

   public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable var1, Class<X1> var2, Class<X2> var3) throws IOException, X1, X2 {
      Preconditions.checkNotNull(var1);
      this.thrown = var1;
      Throwables.propagateIfPossible(var1, IOException.class);
      Throwables.propagateIfPossible(var1, var2, var3);
      throw new RuntimeException(var1);
   }

   @Override
   public void close() throws IOException {
      Throwable var1 = this.thrown;

      while (!this.stack.isEmpty()) {
         Closeable var2 = this.stack.removeFirst();

         try {
            var2.close();
         } catch (Throwable var4) {
            if (var1 == null) {
               var1 = var4;
            } else {
               this.suppressor.suppress(var2, var1, var4);
            }
         }
      }

      if (this.thrown == null && var1 != null) {
         Throwables.propagateIfPossible(var1, IOException.class);
         throw new AssertionError(var1);
      }
   }

   static {
      Closer.SuppressingSuppressor var0 = Closer.SuppressingSuppressor.tryCreate();
      SUPPRESSOR = (Closer.Suppressor)(var0 == null ? Closer.LoggingSuppressor.INSTANCE : var0);
   }

   @VisibleForTesting
   static final class LoggingSuppressor implements Closer.Suppressor {
      static final Closer.LoggingSuppressor INSTANCE = new Closer.LoggingSuppressor();

      @Override
      public void suppress(Closeable var1, Throwable var2, Throwable var3) {
         Logger var10000 = Closeables.logger;
         Level var10001 = Level.WARNING;
         String var4 = String.valueOf(var1);
         var10000.log(
            var10001, new StringBuilder(42 + String.valueOf(var4).length()).append("Suppressing exception thrown when closing ").append(var4).toString(), var3
         );
      }
   }

   @VisibleForTesting
   static final class SuppressingSuppressor implements Closer.Suppressor {
      private final Method addSuppressed;

      static Closer.SuppressingSuppressor tryCreate() {
         Method var0;
         try {
            var0 = Throwable.class.getMethod("addSuppressed", Throwable.class);
         } catch (Throwable var2) {
            return null;
         }

         return new Closer.SuppressingSuppressor(var0);
      }

      private SuppressingSuppressor(Method var1) {
         this.addSuppressed = var1;
      }

      @Override
      public void suppress(Closeable var1, Throwable var2, Throwable var3) {
         if (var2 != var3) {
            try {
               this.addSuppressed.invoke(var2, var3);
            } catch (Throwable var5) {
               Closer.LoggingSuppressor.INSTANCE.suppress(var1, var2, var3);
            }
         }
      }
   }

   @VisibleForTesting
   interface Suppressor {
      void suppress(Closeable var1, Throwable var2, Throwable var3);
   }
}
