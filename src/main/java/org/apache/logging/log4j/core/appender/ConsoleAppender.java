package org.apache.logging.log4j.core.appender;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.CloseShieldOutputStream;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(
   name = "Console",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class ConsoleAppender extends AbstractOutputStreamAppender<OutputStreamManager> {
   public static final String PLUGIN_NAME = "Console";
   private static final String JANSI_CLASS = "org.fusesource.jansi.WindowsAnsiOutputStream";
   private static ConsoleAppender.ConsoleManagerFactory factory = new ConsoleAppender.ConsoleManagerFactory();
   private static final ConsoleAppender.Target DEFAULT_TARGET = ConsoleAppender.Target.SYSTEM_OUT;
   private static final AtomicInteger COUNT = new AtomicInteger();
   private final ConsoleAppender.Target target;

   private ConsoleAppender(
      String var1, Layout<? extends Serializable> var2, Filter var3, OutputStreamManager var4, boolean var5, ConsoleAppender.Target var6, Property[] var7
   ) {
      super(var1, var2, var3, var5, true, var7, var4);
      this.target = var6;
   }

   @Deprecated
   public static ConsoleAppender createAppender(Layout<? extends Serializable> var0, Filter var1, String var2, String var3, String var4, String var5) {
      if (var3 == null) {
         LOGGER.error("No name provided for ConsoleAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         boolean var6 = Boolean.parseBoolean(var4);
         boolean var7 = Booleans.parseBoolean(var5, true);
         ConsoleAppender.Target var8 = var2 == null ? DEFAULT_TARGET : ConsoleAppender.Target.valueOf(var2);
         return new ConsoleAppender(
            var3, (Layout<? extends Serializable>)var0, var1, getManager(var8, var6, false, (Layout<? extends Serializable>)var0), var7, var8, null
         );
      }
   }

   @Deprecated
   public static ConsoleAppender createAppender(
      Layout<? extends Serializable> var0, Filter var1, ConsoleAppender.Target var2, String var3, boolean var4, boolean var5, boolean var6
   ) {
      if (var3 == null) {
         LOGGER.error("No name provided for ConsoleAppender");
         return null;
      } else {
         if (var0 == null) {
            var0 = PatternLayout.createDefaultLayout();
         }

         var2 = var2 == null ? ConsoleAppender.Target.SYSTEM_OUT : var2;
         if (var4 && var5) {
            LOGGER.error("Cannot use both follow and direct on ConsoleAppender");
            return null;
         } else {
            return new ConsoleAppender(
               var3, (Layout<? extends Serializable>)var0, var1, getManager(var2, var4, var5, (Layout<? extends Serializable>)var0), var6, var2, null
            );
         }
      }
   }

   public static ConsoleAppender createDefaultAppenderForLayout(Layout<? extends Serializable> var0) {
      return new ConsoleAppender(
         "DefaultConsole-" + COUNT.incrementAndGet(), var0, null, getDefaultManager(DEFAULT_TARGET, false, false, var0), true, DEFAULT_TARGET, null
      );
   }

   @PluginBuilderFactory
   public static <B extends ConsoleAppender.Builder<B>> B newBuilder() {
      return new ConsoleAppender.Builder<B>().asBuilder();
   }

   private static OutputStreamManager getDefaultManager(ConsoleAppender.Target var0, boolean var1, boolean var2, Layout<? extends Serializable> var3) {
      OutputStream var4 = getOutputStream(var1, var2, var0);
      String var5 = var0.name() + '.' + var1 + '.' + var2 + "-" + COUNT.get();
      return OutputStreamManager.getManager(var5, new ConsoleAppender.FactoryData(var4, var5, var3), factory);
   }

   private static OutputStreamManager getManager(ConsoleAppender.Target var0, boolean var1, boolean var2, Layout<? extends Serializable> var3) {
      OutputStream var4 = getOutputStream(var1, var2, var0);
      String var5 = var0.name() + '.' + var1 + '.' + var2;
      return OutputStreamManager.getManager(var5, new ConsoleAppender.FactoryData(var4, var5, var3), factory);
   }

   private static OutputStream getOutputStream(boolean var0, boolean var1, ConsoleAppender.Target var2) {
      String var3 = Charset.defaultCharset().name();

      CloseShieldOutputStream var12;
      try {
         Object var4 = var2 == ConsoleAppender.Target.SYSTEM_OUT
            ? (var1 ? new FileOutputStream(FileDescriptor.out) : (var0 ? new PrintStream(new ConsoleAppender.SystemOutStream(), true, var3) : System.out))
            : (var1 ? new FileOutputStream(FileDescriptor.err) : (var0 ? new PrintStream(new ConsoleAppender.SystemErrStream(), true, var3) : System.err));
         var12 = new CloseShieldOutputStream((OutputStream)var4);
      } catch (UnsupportedEncodingException var11) {
         throw new IllegalStateException("Unsupported default encoding " + var3, var11);
      }

      PropertiesUtil var5 = PropertiesUtil.getProperties();
      if (var5.isOsWindows() && !var5.getBooleanProperty("log4j.skipJansi", true) && !var1) {
         try {
            Class var6 = Loader.loadClass("org.fusesource.jansi.WindowsAnsiOutputStream");
            Constructor var7 = var6.getConstructor(OutputStream.class);
            return new CloseShieldOutputStream((OutputStream)var7.newInstance(var12));
         } catch (ClassNotFoundException var8) {
            LOGGER.debug("Jansi is not installed, cannot find {}", "org.fusesource.jansi.WindowsAnsiOutputStream");
         } catch (NoSuchMethodException var9) {
            LOGGER.warn("{} is missing the proper constructor", "org.fusesource.jansi.WindowsAnsiOutputStream");
         } catch (Exception var10) {
            LOGGER.warn(
               "Unable to instantiate {} due to {}", "org.fusesource.jansi.WindowsAnsiOutputStream", clean(Throwables.getRootCause(var10).toString()).trim()
            );
         }

         return var12;
      } else {
         return var12;
      }
   }

   private static String clean(String var0) {
      return var0.replace('\u0000', ' ');
   }

   public ConsoleAppender.Target getTarget() {
      return this.target;
   }

   public static class Builder<B extends ConsoleAppender.Builder<B>>
      extends AbstractOutputStreamAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<ConsoleAppender> {
      @PluginBuilderAttribute
      @Required
      private ConsoleAppender.Target target = ConsoleAppender.DEFAULT_TARGET;
      @PluginBuilderAttribute
      private boolean follow;
      @PluginBuilderAttribute
      private boolean direct;

      public B setTarget(ConsoleAppender.Target var1) {
         this.target = var1;
         return this.asBuilder();
      }

      public B setFollow(boolean var1) {
         this.follow = var1;
         return this.asBuilder();
      }

      public B setDirect(boolean var1) {
         this.direct = var1;
         return this.asBuilder();
      }

      public ConsoleAppender build() {
         if (this.follow && this.direct) {
            throw new IllegalArgumentException("Cannot use both follow and direct on ConsoleAppender '" + this.getName() + "'");
         } else {
            Layout var1 = this.getOrCreateLayout(this.target.getDefaultCharset());
            return new ConsoleAppender(
               this.getName(),
               var1,
               this.getFilter(),
               ConsoleAppender.getManager(this.target, this.follow, this.direct, var1),
               this.isIgnoreExceptions(),
               this.target,
               this.getPropertyArray()
            );
         }
      }
   }

   private static class ConsoleManagerFactory implements ManagerFactory<OutputStreamManager, ConsoleAppender.FactoryData> {
      private ConsoleManagerFactory() {
      }

      public OutputStreamManager createManager(String var1, ConsoleAppender.FactoryData var2) {
         return new OutputStreamManager(var2.os, var2.name, var2.layout, true);
      }
   }

   private static class FactoryData {
      private final OutputStream os;
      private final String name;
      private final Layout<? extends Serializable> layout;

      public FactoryData(OutputStream var1, String var2, Layout<? extends Serializable> var3) {
         this.os = var1;
         this.name = var2;
         this.layout = var3;
      }
   }

   private static class SystemErrStream extends OutputStream {
      public SystemErrStream() {
      }

      @Override
      public void close() {
      }

      @Override
      public void flush() {
         System.err.flush();
      }

      @Override
      public void write(byte[] var1) throws IOException {
         System.err.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         System.err.write(var1, var2, var3);
      }

      @Override
      public void write(int var1) {
         System.err.write(var1);
      }
   }

   private static class SystemOutStream extends OutputStream {
      public SystemOutStream() {
      }

      @Override
      public void close() {
      }

      @Override
      public void flush() {
         System.out.flush();
      }

      @Override
      public void write(byte[] var1) throws IOException {
         System.out.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         System.out.write(var1, var2, var3);
      }

      @Override
      public void write(int var1) throws IOException {
         System.out.write(var1);
      }
   }

   public static enum Target {
      SYSTEM_OUT {
         @Override
         public Charset getDefaultCharset() {
            return this.getCharset("sun.stdout.encoding", Charset.defaultCharset());
         }
      },
      SYSTEM_ERR {
         @Override
         public Charset getDefaultCharset() {
            return this.getCharset("sun.stderr.encoding", Charset.defaultCharset());
         }
      };

      private Target() {
      }

      public abstract Charset getDefaultCharset();

      protected Charset getCharset(String var1, Charset var2) {
         return new PropertiesUtil(PropertiesUtil.getSystemProperties()).getCharsetProperty(var1, var2);
      }
   }
}
