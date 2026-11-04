package org.eclipse.aether.internal.impl.slf4j;

import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.Logger;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.sisu.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import xcord.Inject;
import xcord.Named;

@Named("slf4j")
public class Slf4jLoggerFactory implements Service, LoggerFactory {
   private static final boolean AVAILABLE;
   private ILoggerFactory factory;

   public static boolean isSlf4jAvailable() {
      return AVAILABLE;
   }

   public Slf4jLoggerFactory() {
   }

   @Inject
   Slf4jLoggerFactory(@Nullable ILoggerFactory var1) {
      this.setLoggerFactory(var1);
   }

   @Override
   public void initService(ServiceLocator var1) {
      this.setLoggerFactory(var1.getService(ILoggerFactory.class));
   }

   public Slf4jLoggerFactory setLoggerFactory(ILoggerFactory var1) {
      this.factory = var1;
      return this;
   }

   @Override
   public Logger getLogger(String var1) {
      org.slf4j.Logger var2 = this.getFactory().getLogger(var1);
      return (Logger)(var2 instanceof LocationAwareLogger
         ? new Slf4jLoggerFactory.Slf4jLoggerEx((LocationAwareLogger)var2)
         : new Slf4jLoggerFactory.Slf4jLogger(var2));
   }

   private ILoggerFactory getFactory() {
      if (this.factory == null) {
         this.factory = org.slf4j.LoggerFactory.getILoggerFactory();
      }

      return this.factory;
   }

   static {
      boolean var0;
      try {
         Slf4jLoggerFactory.class.getClassLoader().loadClass("org.slf4j.ILoggerFactory");
         var0 = true;
      } catch (LinkageError | Exception var2) {
         var0 = false;
      }

      AVAILABLE = var0;
   }

   private static final class Slf4jLogger implements Logger {
      private final org.slf4j.Logger logger;

      Slf4jLogger(org.slf4j.Logger var1) {
         this.logger = var1;
      }

      @Override
      public boolean isDebugEnabled() {
         return this.logger.isDebugEnabled();
      }

      @Override
      public void debug(String var1) {
         this.logger.debug(var1);
      }

      @Override
      public void debug(String var1, Throwable var2) {
         this.logger.debug(var1, var2);
      }

      @Override
      public boolean isWarnEnabled() {
         return this.logger.isWarnEnabled();
      }

      @Override
      public void warn(String var1) {
         this.logger.warn(var1);
      }

      @Override
      public void warn(String var1, Throwable var2) {
         this.logger.warn(var1, var2);
      }
   }

   private static final class Slf4jLoggerEx implements Logger {
      private static final String FQCN = Slf4jLoggerFactory.Slf4jLoggerEx.class.getName();
      private final LocationAwareLogger logger;

      Slf4jLoggerEx(LocationAwareLogger var1) {
         this.logger = var1;
      }

      @Override
      public boolean isDebugEnabled() {
         return this.logger.isDebugEnabled();
      }

      @Override
      public void debug(String var1) {
         this.logger.log(null, FQCN, 10, var1, null, null);
      }

      @Override
      public void debug(String var1, Throwable var2) {
         this.logger.log(null, FQCN, 10, var1, null, var2);
      }

      @Override
      public boolean isWarnEnabled() {
         return this.logger.isWarnEnabled();
      }

      @Override
      public void warn(String var1) {
         this.logger.log(null, FQCN, 30, var1, null, null);
      }

      @Override
      public void warn(String var1, Throwable var2) {
         this.logger.log(null, FQCN, 30, var1, null, var2);
      }
   }
}
