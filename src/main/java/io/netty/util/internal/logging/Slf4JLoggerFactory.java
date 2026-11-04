package io.netty.util.internal.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class Slf4JLoggerFactory extends InternalLoggerFactory {
   public static final InternalLoggerFactory INSTANCE = new Slf4JLoggerFactory();

   @Deprecated
   public Slf4JLoggerFactory() {
   }

   Slf4JLoggerFactory(boolean var1) {
      assert var1;

      if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
         throw new NoClassDefFoundError("NOPLoggerFactory not supported");
      }
   }

   @Override
   public InternalLogger newInstance(String var1) {
      return wrapLogger(LoggerFactory.getLogger(var1));
   }

   static InternalLogger wrapLogger(Logger var0) {
      return (InternalLogger)(var0 instanceof LocationAwareLogger ? new LocationAwareSlf4JLogger((LocationAwareLogger)var0) : new Slf4JLogger(var0));
   }

   static InternalLoggerFactory getInstanceWithNopCheck() {
      return Slf4JLoggerFactory.NopInstanceHolder.INSTANCE_WITH_NOP_CHECK;
   }

   private static final class NopInstanceHolder {
      private static final InternalLoggerFactory INSTANCE_WITH_NOP_CHECK = new Slf4JLoggerFactory(true);
   }
}
