package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

public final class Logs {
   public static final String NEW_LINE;
   private static final String SISU = "Sisu";
   private static final Logs.Sink SINK;
   public static final boolean TRACE_ENABLED;

   static {
      String var0;
      boolean var1;
      try {
         var0 = System.getProperty("line.separator", "\n");
         String var2 = System.getProperty("sisu.debug", "false");
         var1 = "".equals(var2) || "true".equalsIgnoreCase(var2);
      } catch (RuntimeException var5) {
         var0 = "\n";
         var1 = false;
      }

      NEW_LINE = var0;

      Object var6;
      try {
         var6 = var1 ? new Logs.ConsoleSink() : new Logs.SLF4JSink();
      } catch (RuntimeException var3) {
         var6 = new Logs.JULSink();
      } catch (LinkageError var4) {
         var6 = new Logs.JULSink();
      }

      SINK = (Logs.Sink)var6;
      TRACE_ENABLED = SINK.isTraceEnabled();
   }

   private Logs() {
   }

   public static void trace(String var0, Object var1, Object var2) {
      if (TRACE_ENABLED) {
         SINK.trace(format(format(var0, var1), var2), var2 instanceof Throwable ? (Throwable)var2 : null);
      }
   }

   public static void warn(String var0, Object var1, Object var2) {
      SINK.warn(format(format(var0, var1), var2), var2 instanceof Throwable ? (Throwable)var2 : null);
   }

   public static void catchThrowable(Throwable var0) {
      for (Throwable var1 = var0; var1 != null; var1 = var1.getCause()) {
         if (var1 instanceof ThreadDeath || var1 instanceof VirtualMachineError) {
            throw (Error)var1;
         }
      }
   }

   public static void throwUnchecked(Throwable var0) {
      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else if (var0 instanceof Error) {
         throw (Error)var0;
      } else {
         throw (RuntimeException)RuntimeException.class.cast(new ProvisionException(var0.toString(), var0));
      }
   }

   public static String identityToString(Object var0) {
      return var0 == null ? null : var0.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(var0));
   }

   public static String toString(Module var0) {
      StringBuilder var1 = new StringBuilder(identityToString(var0));
      var1.append(NEW_LINE).append(NEW_LINE);
      var1.append("-----[elements]----------------------------------------------------------------").append(NEW_LINE);
      int var2 = 0;

      for (Element var3 : Elements.getElements(new Module[]{var0})) {
         var1.append(var2++).append(". ").append(var3).append(NEW_LINE);
      }

      return var1.append("-------------------------------------------------------------------------------").append(NEW_LINE).toString();
   }

   public static String toString(Injector var0) {
      StringBuilder var1 = new StringBuilder(identityToString(var0));
      if (var0.getParent() != null) {
         var1.append(" parent: ").append(identityToString(var0.getParent()));
      }

      var1.append(NEW_LINE).append(NEW_LINE);
      var1.append("-----[explicit bindings]-------------------------------------------------------").append(NEW_LINE);
      int var2 = 0;
      Map var3 = var0.getBindings();

      for (Binding var4 : var3.values()) {
         var1.append(var2++).append(". ").append(var4).append(NEW_LINE);
      }

      var1.append("-----[implicit bindings]-------------------------------------------------------").append(NEW_LINE);

      for (Binding var6 : var0.getAllBindings().values()) {
         if (!var3.containsKey(var6.getKey())) {
            var1.append(var2++).append(". ").append(var6).append(NEW_LINE);
         }
      }

      return var1.append("-------------------------------------------------------------------------------").append(NEW_LINE).toString();
   }

   private static String format(String var0, Object var1) {
      int var2 = var0.length();
      boolean var3 = true;
      int var4 = 0;

      for (char var5 = ' '; var4 < var2; var4++) {
         char var6 = var0.charAt(var4);
         if (var5 == '{' && var6 == '}') {
            break;
         }

         if (var5 == '<' && var6 == '>') {
            var3 = false;
            break;
         }

         var5 = var6;
      }

      if (var4 >= var2) {
         return var0;
      } else {
         StringBuilder var10 = new StringBuilder();
         if (--var4 > 0) {
            var10.append(var0.substring(0, var4));
         }

         try {
            var10.append(var3 ? var1 : identityToString(var1));
         } catch (RuntimeException var7) {
            var10.append(var7);
         }

         var4 += 2;
         if (var4 < var2) {
            var10.append(var0.substring(var4, var2));
         }

         return var10.toString();
      }
   }

   static final class ConsoleSink implements Logs.Sink {
      private static final String TRACE = "TRACE: Sisu - ";
      private static final String WARN = "WARN: Sisu - ";

      @Override
      public boolean isTraceEnabled() {
         return true;
      }

      @Override
      public void trace(String var1, Throwable var2) {
         System.out.println("TRACE: Sisu - " + var1);
         if (var2 != null) {
            var2.printStackTrace(System.out);
         }
      }

      @Override
      public void warn(String var1, Throwable var2) {
         System.err.println("WARN: Sisu - " + var1);
         if (var2 != null) {
            var2.printStackTrace(System.err);
         }
      }
   }

   static final class JULSink implements Logs.Sink {
      private static final Logger logger = Logger.getLogger("Sisu");

      @Override
      public boolean isTraceEnabled() {
         return logger.isLoggable(Level.FINER);
      }

      @Override
      public void trace(String var1, Throwable var2) {
         logger.log(Level.FINER, var1, var2);
      }

      @Override
      public void warn(String var1, Throwable var2) {
         logger.log(Level.WARNING, var1, var2);
      }
   }

   static final class SLF4JSink implements Logs.Sink {
      private static final org.slf4j.Logger logger = LoggerFactory.getLogger("Sisu");

      @Override
      public boolean isTraceEnabled() {
         return logger.isTraceEnabled();
      }

      @Override
      public void trace(String var1, Throwable var2) {
         logger.trace(var1, var2);
      }

      @Override
      public void warn(String var1, Throwable var2) {
         logger.warn(var1, var2);
      }
   }

   private interface Sink {
      boolean isTraceEnabled();

      void trace(String var1, Throwable var2);

      void warn(String var1, Throwable var2);
   }
}
