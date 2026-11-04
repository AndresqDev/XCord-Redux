package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class LoggingDnsQueryLifeCycleObserverFactory implements DnsQueryLifecycleObserverFactory {
   private static final InternalLogger DEFAULT_LOGGER = InternalLoggerFactory.getInstance(LoggingDnsQueryLifeCycleObserverFactory.class);
   private final InternalLogger logger;
   private final InternalLogLevel level;

   public LoggingDnsQueryLifeCycleObserverFactory() {
      this(LogLevel.DEBUG);
   }

   public LoggingDnsQueryLifeCycleObserverFactory(LogLevel var1) {
      this.level = checkAndConvertLevel(var1);
      this.logger = DEFAULT_LOGGER;
   }

   public LoggingDnsQueryLifeCycleObserverFactory(Class<?> var1, LogLevel var2) {
      this.level = checkAndConvertLevel(var2);
      this.logger = InternalLoggerFactory.getInstance(ObjectUtil.checkNotNull(var1, "classContext"));
   }

   public LoggingDnsQueryLifeCycleObserverFactory(String var1, LogLevel var2) {
      this.level = checkAndConvertLevel(var2);
      this.logger = InternalLoggerFactory.getInstance(ObjectUtil.checkNotNull(var1, "name"));
   }

   private static InternalLogLevel checkAndConvertLevel(LogLevel var0) {
      return ObjectUtil.checkNotNull(var0, "level").toInternalLevel();
   }

   @Override
   public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1) {
      return new LoggingDnsQueryLifecycleObserver(var1, this.logger, this.level);
   }
}
