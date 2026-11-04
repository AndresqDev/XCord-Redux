package net.minecrell.terminalconsole;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

@Plugin(
   name = "TerminalConsole",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class TerminalConsoleAppender extends AbstractAppender {
   public static final String PLUGIN_NAME = "TerminalConsole";
   static final String PROPERTY_PREFIX = "terminal";
   public static final String JLINE_OVERRIDE_PROPERTY = "terminal.jline";
   public static final String ANSI_OVERRIDE_PROPERTY = "terminal.ansi";
   @Nullable
   private static final Boolean ANSI_OVERRIDE = getOptionalBooleanProperty("terminal.ansi");
   private static final PrintStream stdout = System.out;
   private static boolean initialized;
   @Nullable
   private static Terminal terminal;
   @Nullable
   private static LineReader reader;

   @Nullable
   public static synchronized Terminal getTerminal() {
      return terminal;
   }

   @Nullable
   public static synchronized LineReader getReader() {
      return reader;
   }

   public static synchronized void setReader(@Nullable LineReader var0) {
      if (var0 != null && var0.getTerminal() != terminal) {
         throw new IllegalArgumentException("Reader was not created with TerminalConsoleAppender.getTerminal()");
      } else {
         reader = var0;
      }
   }

   public static boolean isAnsiSupported() {
      return ANSI_OVERRIDE != null ? ANSI_OVERRIDE : terminal != null;
   }

   protected TerminalConsoleAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4) {
      super(var1, var2, var3, var4);
      initializeTerminal();
   }

   private static synchronized void initializeTerminal() {
      if (!initialized) {
         initialized = true;
         Boolean var0 = getOptionalBooleanProperty("terminal.jline");
         boolean var1 = var0 == Boolean.TRUE || System.getProperty("java.class.path").contains("idea_rt.jar");
         if (var0 != Boolean.FALSE) {
            try {
               terminal = TerminalBuilder.builder().dumb(var1).build();
            } catch (IllegalStateException var3) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.warn("Advanced terminal features are not available in this environment", var3);
               } else {
                  LOGGER.warn("Advanced terminal features are not available in this environment");
               }
            } catch (IOException var4) {
               LOGGER.error("Failed to initialize terminal. Falling back to standard console", var4);
            }
         }
      }
   }

   public void append(LogEvent var1) {
      print(this.getLayout().toSerializable(var1).toString());
   }

   private static synchronized void print(String var0) {
      if (terminal != null) {
         if (reader != null) {
            reader.printAbove(var0);
         } else {
            terminal.writer().print(var0);
            terminal.writer().flush();
         }
      } else {
         stdout.print(var0);
      }
   }

   public static synchronized void close() throws IOException {
      if (initialized) {
         initialized = false;
         reader = null;
         if (terminal != null) {
            try {
               terminal.close();
            } finally {
               terminal = null;
            }
         }
      }
   }

   @PluginFactory
   public static TerminalConsoleAppender createAppender(
      @Required(message = "No name provided for TerminalConsoleAppender") @PluginAttribute("name") String var0,
      @PluginElement("Filter") Filter var1,
      @PluginElement("Layout") @Nullable Layout<? extends Serializable> var2,
      @PluginAttribute(value = "ignoreExceptions",defaultBoolean = true) boolean var3
   ) {
      if (var2 == null) {
         var2 = PatternLayout.createDefaultLayout();
      }

      return new TerminalConsoleAppender(var0, var1, (Layout<? extends Serializable>)var2, var3);
   }

   @Nullable
   private static Boolean getOptionalBooleanProperty(String var0) {
      String var1 = PropertiesUtil.getProperties().getStringProperty(var0);
      if (var1 == null) {
         return null;
      } else if (var1.equalsIgnoreCase("true")) {
         return Boolean.TRUE;
      } else if (var1.equalsIgnoreCase("false")) {
         return Boolean.FALSE;
      } else {
         LOGGER.warn("Invalid value for boolean input property '{}': {}", var0, var1);
         return null;
      }
   }
}
