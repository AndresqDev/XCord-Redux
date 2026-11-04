package org.apache.logging.log4j.core.config.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.status.StatusConsoleListener;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.status.StatusLogger;

public class StatusConfiguration {
   private static final PrintStream DEFAULT_STREAM = System.out;
   private static final Level DEFAULT_STATUS = Level.ERROR;
   private static final StatusConfiguration.Verbosity DEFAULT_VERBOSITY = StatusConfiguration.Verbosity.QUIET;
   private final Collection<String> errorMessages = new LinkedBlockingQueue<>();
   private final StatusLogger logger = StatusLogger.getLogger();
   private volatile boolean initialized;
   private PrintStream destination = DEFAULT_STREAM;
   private Level status = DEFAULT_STATUS;
   private StatusConfiguration.Verbosity verbosity = DEFAULT_VERBOSITY;
   private String[] verboseClasses;

   public void error(String var1) {
      if (!this.initialized) {
         this.errorMessages.add(var1);
      } else {
         this.logger.error(var1);
      }
   }

   public StatusConfiguration withDestination(String var1) {
      try {
         this.destination = this.parseStreamName(var1);
      } catch (URISyntaxException var3) {
         this.error("Could not parse URI [" + var1 + "]. Falling back to default of stdout.");
         this.destination = DEFAULT_STREAM;
      } catch (FileNotFoundException var4) {
         this.error("File could not be found at [" + var1 + "]. Falling back to default of stdout.");
         this.destination = DEFAULT_STREAM;
      }

      return this;
   }

   private PrintStream parseStreamName(String var1) throws URISyntaxException, FileNotFoundException {
      if (var1 == null || var1.equalsIgnoreCase("out")) {
         return DEFAULT_STREAM;
      } else if (var1.equalsIgnoreCase("err")) {
         return System.err;
      } else {
         URI var2 = NetUtils.toURI(var1);
         File var3 = FileUtils.fileFromUri(var2);
         if (var3 == null) {
            return DEFAULT_STREAM;
         } else {
            FileOutputStream var4 = new FileOutputStream(var3);
            return new PrintStream(var4, true);
         }
      }
   }

   public StatusConfiguration withStatus(String var1) {
      this.status = Level.toLevel(var1, null);
      if (this.status == null) {
         this.error("Invalid status level specified: " + var1 + ". Defaulting to ERROR.");
         this.status = Level.ERROR;
      }

      return this;
   }

   public StatusConfiguration withStatus(Level var1) {
      this.status = var1;
      return this;
   }

   public StatusConfiguration withVerbosity(String var1) {
      this.verbosity = StatusConfiguration.Verbosity.toVerbosity(var1);
      return this;
   }

   public StatusConfiguration withVerboseClasses(String... var1) {
      this.verboseClasses = var1;
      return this;
   }

   public void initialize() {
      if (!this.initialized) {
         if (this.status == Level.OFF) {
            this.initialized = true;
         } else {
            boolean var1 = this.configureExistingStatusConsoleListener();
            if (!var1) {
               this.registerNewStatusConsoleListener();
            }

            this.migrateSavedLogMessages();
         }
      }
   }

   private boolean configureExistingStatusConsoleListener() {
      boolean var1 = false;

      for (StatusListener var3 : this.logger.getListeners()) {
         if (var3 instanceof StatusConsoleListener) {
            StatusConsoleListener var4 = (StatusConsoleListener)var3;
            var4.setLevel(this.status);
            this.logger.updateListenerLevel(this.status);
            if (this.verbosity == StatusConfiguration.Verbosity.QUIET) {
               var4.setFilters(this.verboseClasses);
            }

            var1 = true;
         }
      }

      return var1;
   }

   private void registerNewStatusConsoleListener() {
      StatusConsoleListener var1 = new StatusConsoleListener(this.status, this.destination);
      if (this.verbosity == StatusConfiguration.Verbosity.QUIET) {
         var1.setFilters(this.verboseClasses);
      }

      this.logger.registerListener(var1);
   }

   private void migrateSavedLogMessages() {
      for (String var2 : this.errorMessages) {
         this.logger.error(var2);
      }

      this.initialized = true;
      this.errorMessages.clear();
   }

   public static enum Verbosity {
      QUIET,
      VERBOSE;

      public static StatusConfiguration.Verbosity toVerbosity(String var0) {
         return Boolean.parseBoolean(var0) ? VERBOSE : QUIET;
      }
   }
}
